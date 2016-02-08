package y.em;

import y.geom.Point3d;

public class SiteDBInfo
{
	public static final String SEPARATOR = "#";
	
	private String operatore;
	private String indirizzo;
	private String comune;
	private String provincia;
	
	private Point3d position;
	
	private String codiceSito;	// GO02
	private String stato;		// F
	private String realizzato;	// REALIZZATO
	private String catasto;
	private String note;
	
	private String proto_in;
	private String data_proto_in;
	private String proto_out;
	private String data_proto_out;
	private String data_attivazione;
	private String data_realizzazione;

	public SiteDBInfo()
	{
		this.position = new Point3d();
		operatore = "";
		indirizzo = "";
		comune = "";
		provincia = "";
		codiceSito = "";
		stato = "";
		realizzato = "";
		note = "";
		catasto = "";
		
		proto_in = "";
		data_proto_in = "";
		proto_out = "";
		data_proto_out = "";
		data_attivazione = "";
		data_realizzazione ="";
	}

	public String getOperatore() {
		return operatore;
	}

	public void setOperatore(String operatore) {
		this.operatore = operatore;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getComune() {
		return comune;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public Point3d getPosition() {
		return position;
	}

	public void setPosition(Point3d position) {
		this.position = position;
	}

	public String getCodiceSito() {
		return codiceSito;
	}

	public void setCodiceSito(String codiceSito) {
		this.codiceSito = codiceSito;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getRealizzato() {
		return realizzato;
	}

	public void setRealizzato(String realizzato) {
		this.realizzato = realizzato;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
	
	public String getProto_in() {
		return proto_in;
	}

	public void setProto_in(String proto_in) {
		this.proto_in = proto_in;
	}

	public String getData_proto_in() {
		return data_proto_in;
	}

	public void setData_proto_in(String data_proto_in) {
		this.data_proto_in = data_proto_in;
	}

	public String getProto_out() {
		return proto_out;
	}

	public void setProto_out(String proto_out) {
		this.proto_out = proto_out;
	}

	public String getData_proto_out() {
		return data_proto_out;
	}

	public void setData_proto_out(String data_proto_out) {
		this.data_proto_out = data_proto_out;
	}

	public String getData_attivazione() {
		return data_attivazione;
	}

	public void setData_attivazione(String data_attivazione) {
		this.data_attivazione = data_attivazione;
	}

	public String getData_realizzazione() {
		return data_realizzazione;
	}

	public void setData_realizzazione(String data_realizzazione) {
		this.data_realizzazione = data_realizzazione;
	}

	public String getCatasto() {
		return catasto;
	}

	public void setCatasto(String catasto) {
		this.catasto = catasto;
	}

	public String toString()
	{
		return operatore + SEPARATOR + indirizzo + SEPARATOR + comune + SEPARATOR + provincia + SEPARATOR + codiceSito + SEPARATOR + stato
				 + SEPARATOR + realizzato + SEPARATOR + note + SEPARATOR + position.getX() + SEPARATOR + position.getY() + SEPARATOR + position.getZ()
				  + SEPARATOR + catasto + SEPARATOR + getProto_in() + SEPARATOR + getData_proto_in() + SEPARATOR + getProto_out()
				   + SEPARATOR + getData_proto_out() + SEPARATOR + getData_attivazione() + SEPARATOR + getData_realizzazione();
	}
	
	public static SiteDBInfo parseFrom(String s)
	{
		final String[] parts = s.split(SEPARATOR, -1);
		int argi = 0;
		SiteDBInfo info = new SiteDBInfo();
		info.operatore = parts[argi++];
		info.indirizzo = parts[argi++];
		info.comune = parts[argi++];
		info.provincia = parts[argi++];
		info.codiceSito = parts[argi++];
		info.stato = parts[argi++];
		info.realizzato = parts[argi++];
		info.note = parts[argi++];
		final double x = Double.parseDouble(parts[argi++]);
		final double y = Double.parseDouble(parts[argi++]);
		final double z = Double.parseDouble(parts[argi++]);
		info.position = new Point3d(x, y, z);
		info.catasto = parts[argi++];
		info.setProto_in(parts[argi++]);
		info.setData_proto_in(parts[argi++]);
		info.setProto_out(parts[argi++]);
		info.setData_proto_out(parts[argi++]);
		info.setData_attivazione(parts[argi++]);
		info.setData_realizzazione(parts[argi++]);
		
		return info;
	}

	public boolean hasProtocolInformations() {
		return !proto_in.isEmpty() || !data_proto_in.isEmpty() || !proto_out.isEmpty() ||
		!data_proto_out.isEmpty() || !data_attivazione.isEmpty() || !data_realizzazione.isEmpty();
	}
}
