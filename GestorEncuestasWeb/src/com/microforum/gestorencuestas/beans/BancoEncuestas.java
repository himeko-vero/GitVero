package com.microforum.gestorencuestas.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.microforum.gestorencuestas.entities.Encuesta;
import com.microforum.gestorencuestas.entities.Pregunta;

@ManagedBean
@ApplicationScoped
public class BancoEncuestas {
	private List<SelectItem> arrayEncuestas;
	String referenciaEncuesta;
	Collection<Pregunta> preguntasEncuesta;
	String textoPregunta;
	int tipoPregunta;
	//Métodos get y set
	public List<SelectItem> getArrayEncuestas() {
		return arrayEncuestas;
	}

	public void setArrayEncuestas(List<SelectItem> arrayEncuestas) {
		this.arrayEncuestas = arrayEncuestas;
	}
	
	public String getTextoPregunta() {
		return textoPregunta;
	}

	public void setTextoPregunta(String textoPregunta) {
		this.textoPregunta = textoPregunta;
	}
	
	public Collection<Pregunta> getPreguntasEncuesta() {
		return preguntasEncuesta;
	}

	public void setPreguntasEncuesta(Collection<Pregunta> preguntasEncuesta) {
		this.preguntasEncuesta = preguntasEncuesta;
	}

	//Método para llenar la lista de selección de encuestas
	public BancoEncuestas(){
		Configuration conf=new Configuration();
		SessionFactory sf=conf.configure().buildSessionFactory();
		Session session=sf.openSession();
		Query query=session.createQuery("FROM Encuesta");
		List<Encuesta> listadoEncuestas=query.list();
		arrayEncuestas=new ArrayList();
		arrayEncuestas.add(new SelectItem("----Encuestas----"));
		for(Encuesta e:listadoEncuestas){
			String refEncuesta=e.getRef();
			/*Query query=session.createQuery("FROM Usuario WHERE ");
			String autorEncuesta=e.getAutor();*/
			String propositoEncuesta=e.getProposito();
			SelectItem item=new SelectItem(refEncuesta,refEncuesta + ": " + propositoEncuesta);
			arrayEncuestas.add(item);			
		}
		session.close();
	}
	//Método para obtener la referencia de la encuesta seleccionada
	public void obetenerRefEncuesta(ValueChangeEvent e){			
		referenciaEncuesta=(String) e.getNewValue();
	}
	//Método para disponer la encuesta seleccionada
	public void disponerEncuesta(ActionEvent e){	
		Configuration conf=new Configuration();
		SessionFactory sf=conf.configure().buildSessionFactory();
		Session session=sf.openSession();
		//Query query=session.createQuery("SELECT preguntas FROM Encuesta WHERE ref LIKE " + referenciaEncuesta);
		Encuesta encuesta=(Encuesta) session.get(Encuesta.class,referenciaEncuesta);
		preguntasEncuesta=encuesta.getPreguntas();
		
	}	
}
