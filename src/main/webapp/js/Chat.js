class Chat {
	constructor(ko, userConnected) {
		let self = this;
		this.ko = ko;
		
		this.userConnected = userConnected;
		this.estado = ko.observable("");
		this.error = ko.observable();
		
		this.usuarios = ko.observableArray([]);
		this.mensajesRecibidos = ko.observableArray([]);
		this.conversaciones = ko.observableArray([]);
		
		this.destinatario = ko.observable();
		this.mensajeQueVoyAEnviar = ko.observable();
		
		this.chat = new WebSocket("wss://" + window.location.host + "/wsTexto");
		
		this.chat.onopen = function() {
			self.estado("Conectado al chat de texto");
			self.error("");
		}

		this.chat.onerror = function() {
			self.estado("");
			self.error("Chat de texto cerrado");
		}

		this.chat.onclose = function() {
			self.estado("");
			self.error("Chat de texto cerrado");
		}
		
		this.chat.onmessage = function(event) {
			var data = JSON.parse(event.data);
			if (data.type == "FOR ALL") {
				var mensaje = new Mensaje(data.message, data.time, data.remitente);
				self.mensajesRecibidos.push(mensaje);
			} else if (data.type == "ARRIVAL") {
				self.addUsuario(data.userName, data.picture);
			} else if (data.type == "BYE") {
				var userName = data.userName;
				for (var i=0; i<self.usuarios().length; i++) {
					if (self.usuarios()[i].nombre == userName) {
						self.usuarios.splice(i, 1);
						break;
					}
				}
			} else if (data.type == "PARTICULAR") {
				var conversacionActual = self.buscarConversacion(data.remitente);
				if (conversacionActual!=null) {
					var mensaje = new Mensaje(data.message.message, data.message.time, data.remitente);
					conversacionActual.addMensaje(mensaje);
				} else {
					conversacionActual = new Conversacion(ko, data.remitente, self);
					var mensaje = new Mensaje(data.message.message, data.message.time, data.remitente);
					conversacionActual.addMensaje(mensaje);
					self.conversaciones.push(conversacionActual);
				}
				self.ponerVisible(data.remitente);
			} 
		}

	}
	
	close() {
		this.chat.close();
	}
	
	enviar(mensaje) {
		this.chat.send(JSON.stringify(mensaje));
	}
	
	enviarATodos() {
		var mensaje = {
			type : "BROADCAST",
			message : this.mensajeQueVoyAEnviar()
		};
		this.chat.send(JSON.stringify(mensaje));
	}
	
	buscarConversacion(nombreInterlocutor) {
		for (var i=0; i<this.conversaciones().length; i++) {
			if (this.conversaciones()[i].nombreInterlocutor==nombreInterlocutor)
				return this.conversaciones()[i];
		}
		return null;
	}
	
	setDestinatario(interlocutor) {
		this.destinatario(interlocutor);
		var conversacion = this.buscarConversacion(interlocutor.nombre);
		if (conversacion==null) {
			conversacion = new Conversacion(this.ko, interlocutor.nombre, this);
			this.conversaciones.push(conversacion);
		}
		this.ponerVisible(interlocutor.nombre);
	}
	
	ponerVisible(nombreInterlocutor) {
		for (var i=0; i<this.conversaciones().length; i++) {
			var conversacion = this.conversaciones()[i];
			conversacion.visible(conversacion.nombreInterlocutor == nombreInterlocutor);
		}
	}
	
	addUsuario(userName, picture) {
		var add = true;
		for (var i = 0; i < this.usuarios().length; i++) {
			if (this.usuarios()[i].nombre == userName) {
				add = false;
			}
		}
		if (add){
			this.usuarios.push(new Usuario(userName, picture));
		}
	}
	
	recuperarConversacion() {
        var msgRecuperados = new Array();
        var infoMsg = {
            sender: this.userConnected().name,
            recipient: this.destinatario().nombre
        };
        var data = {
            data: JSON.stringify(infoMsg),
            url: "users/obtenerConversacion",
            type: "post",
            async: false,
            contentType: 'application/json',
            success: function(response) {
                for (var i = 0; i < response.length; i++) {
                    var date = new Date(response[i].date);
                    var stringDate = date.getDate() + "-" + (date.getMonth() + 1) + "-" + date.getFullYear() + ", " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
                    var message = response[i].message;
                    var mensaje = new Mensaje(message, stringDate, response[i].sender);

                    msgRecuperados.push(mensaje);
                }
            }
        };

        $.ajax(data);
        return msgRecuperados;
    }
	
}
	