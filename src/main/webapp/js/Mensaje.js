class Mensaje {
	constructor(texto, hora, remitente) {
		this.texto = texto;
		var date = new Date(Date.now());
		this.hora = hora ? hora : date.getDate() + "-" + (date.getMonth() + 1) + "-" + date.getFullYear() + ", " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
		this.remitente = remitente;
	}
}