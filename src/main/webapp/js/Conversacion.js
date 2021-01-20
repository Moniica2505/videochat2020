class Conversacion {
	constructor(ko, nombreInterlocutor, chat) {
		this.nombreInterlocutor = nombreInterlocutor;
		this.mensajes = ko.observableArray([]);
		this.textoAEnviar = ko.observable("");
		this.chat = chat;
		this.visible = ko.observable(true);
	}
	
	addMensaje(mensaje) {
		this.mensajes.push(mensaje);
	}
	
	enviar() {
		var mensaje = {
			type : "PARTICULAR",
			destinatario : this.nombreInterlocutor,
			texto : this.textoAEnviar()
		};
		this.chat.enviar(mensaje);
		var mensaje = new Mensaje(this.textoAEnviar());
		this.addMensaje(mensaje);
	}
	
	obtenerConversacion() {
		var conversacionesObtenidas = this.chat.recuperarConversacion();
		for (var i = 0; i < conversacionesObtenidas.length; i++) {
			this.mensajes.push(conversacionesObtenidas[i]);
		}
	}
}