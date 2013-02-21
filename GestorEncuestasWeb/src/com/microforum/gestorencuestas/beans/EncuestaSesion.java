package com.microforum.gestorencuestas.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.microforum.gestorencuestas.entities.Administrador;
import com.microforum.gestorencuestas.entities.Encuesta;
import com.microforum.gestorencuestas.entities.Pregunta;
import com.microforum.gestorencuestas.entities.Usuario;

/**
 * @author alumno
 *
 */
@ManagedBean
@SessionScoped
public class EncuestaSesion {
	private List<String> preguntasEncuesta=new ArrayList<String>();
	private List<RegistroEncuesta> registrosEncuesta=new ArrayList<RegistroEncuesta>();
	private String nuevaPregunta="";
	private String visibilidadDetalle="display: none;";
	private String proposito;	
	private String nombre;
	private int valoracion;
	
	private List<String> getRefList(List<RegistroEncuesta> regList){
		List<String>refList=new ArrayList<String>();
		for(RegistroEncuesta re:regList){
			refList.add(re.getRef());		
		}
		return refList;
	}
	
	public void validarNombre(FacesContext context, UIComponent commponentToValidate,Object value) throws ValidatorException{
		String nombre=(String) value;
		if(!nombre.startsWith("Encuesta")){
			FacesMessage msg=new FacesMessage("El nombre debe empezar por Encuesta");
			throw new ValidatorException(msg);
		}
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		if(!nombre.startsWith("Encuesta")){
			FacesMessage fc=new FacesMessage("El nombre no es v�lido. Debe tener el formato Encuesta<X>");
			FacesContext ctx=FacesContext.getCurrentInstance();
			ctx.addMessage("nombreE", fc);
		}else
			this.nombre = nombre;
	}

	public int getValoracion() {
		return valoracion;
	}

	public void setValoracion(int valoracion) {
		if(valoracion<0 || valoracion>10){
			FacesMessage fc=new FacesMessage("La valoraci�n debe estar entre 0 y 10;");
			FacesContext ctx=FacesContext.getCurrentInstance();
			ctx.addMessage("importanciaE", fc);
		}
		this.valoracion = valoracion;
	}

	public String getProposito() {
		return proposito;
	}

	public void setProposito(String proposito) {
		this.proposito = proposito;
	}
	//Para incluir un error ��por si hay!!
	public String iniciarEncuesta(){
		/*FacesMessage fc=new FacesMessage("ERROR");
		FacesContext ctx=FacesContext.getCurrentInstance();
					//(MensajeAsociadoA...(AlContexto en este caso), mensaje)
		ctx.addMessage(null, fc);
		
		
		//si devuelve null, no nos movemos de la pagina ^.^
		return null;*/
		return "DetalleEncuesta";
	}
	
	
	
	public String cancelarEncuesta(){
		System.out.println("Cancelando...");
		
		//Cerrar la sesi�n
		FacesContext fc=FacesContext.getCurrentInstance();
		ExternalContext ec=fc.getExternalContext();
		HttpServletRequest req= (HttpServletRequest) ec.getRequest();
		HttpSession session= req.getSession();
		session.invalidate();
		
		return "index.jsp";
		
	}
	
	public String guardarEncuesta(){
		System.out.println("Guardando encuesta...");
		
		FacesContext fContext=FacesContext.getCurrentInstance();
		ExternalContext ec=fContext.getExternalContext();
		HttpServletRequest request=(HttpServletRequest) ec.getRequest();
		HttpSession session=request.getSession();
		Object obj=session.getAttribute("userBean");
		if(obj==null){
			session.invalidate();
			return "index.jsp";
		}else{
			UsuarioAutenticado authUser=(UsuarioAutenticado) obj;
			Administrador user=(Administrador) authUser.getUser();
			Configuration conf=new Configuration();
			SessionFactory sf=conf.configure().buildSessionFactory();
			Session hSession=sf.openSession();
			Transaction tr=hSession.beginTransaction();
			Query query=hSession.createQuery("FROM Pregunta WHERE ref IN (:preguntaRef)");
			List<String> refList=getRefList(registrosEncuesta);
			query.setParameterList("preguntaRef", refList);
			List<Pregunta> preguntas=query.list();
			//Hacemos que el objeto quede vinculado a la sesi�n
			hSession.merge(user);
			Encuesta encuesta=new Encuesta();
			encuesta.setAutor(user);
			encuesta.setProposito(proposito);
			
			System.out.println("Autor: " + user + " Proposito: " + proposito);
			
			for(Pregunta p:preguntas){
				encuesta.getPreguntas().add(p);
			}
			hSession.save(encuesta);
			tr.commit();
			hSession.close();		
			return "/encuestas/administracion/administracionIndex";	
		}
	}
	
	public String volverEncuesta(){
		System.out.println("Volviendo a DetalleEncuesta...");
		return "DetalleEncuesta";
	}
	
	
	public String terminarEncuesta(){
		//Agregar control de navegaci�n;
		
			return "resumenEncuesta";
		
		
	}
	
	
	public String getNuevaPregunta() {
		return nuevaPregunta;
	}

	public void setNuevaPregunta(String nuevaPregunta) {
		this.nuevaPregunta = nuevaPregunta;
	}

	public List<String> getPreguntasEncuesta() {
		return preguntasEncuesta;
	}

	public void setPreguntasEncuesta(List<String> preguntasEncuesta) {
		this.preguntasEncuesta = preguntasEncuesta;
	}
	
	public void addPregunta(ActionEvent e){
		System.out.println("ActionEvent");
	}
							//Acci�n en el cambio del valor en la lista desplegable
	public void selectPregunta(ValueChangeEvent e){
		System.out.println(e.getNewValue());
		System.out.println("ValueChangeEvent");
		String ref=(String) e.getNewValue();
		Configuration conf=new Configuration();
		SessionFactory sf=conf.configure().buildSessionFactory();
		Session session=sf.openSession();
		Pregunta p=(Pregunta) session.get(Pregunta.class, ref);
		if(p!=null){
			visibilidadDetalle="display: inline-block";
			RegistroEncuesta re=new RegistroEncuesta();
			re.setRef(p.getRef());
			re.setTexto(p.getTexto());
			re.setTipo(re.getTipo());
			registrosEncuesta.add(re);
			session.close();
		}
		//preguntasEncuesta.add((String) e.getNewValue());
	}
	
	//Mi forma de borrar U.U"
										//Acci�n de un boton
	public void eliminarRegistrosEncuesta(ActionEvent e){
		RegistroEncuesta reg=(RegistroEncuesta) ((UIParameter)e.getComponent().findComponent("refPregunta")).getValue();
		registrosEncuesta.remove(reg);
		if(registrosEncuesta.isEmpty())
			visibilidadDetalle="display: none;";	
	}
	
	//Forma de borrar del profe
	public void eliminarPregunta(ActionEvent e){
		UIComponent component=e.getComponent();
		component=component.findComponent("preguntaRef");
		if (component!=null){
			UIParameter parameter=(UIParameter) component;
			//Como el valor es de tipo RegistroEncuesta (tiene ya el identificador, texto y tipo) pues hale! directamente...
			RegistroEncuesta reg=(RegistroEncuesta) parameter.getValue();
			registrosEncuesta.remove(reg);
			if(registrosEncuesta.isEmpty())
				visibilidadDetalle="display: none;";
		}
	}
	
	public List<RegistroEncuesta> getRegistrosEncuesta() {
		return registrosEncuesta;
	}

	public void setRegistrosEncuesta(List<RegistroEncuesta> registrosEncuesta) {
		this.registrosEncuesta = registrosEncuesta;
	}

	public String getVisibilidadDetalle() {
		return visibilidadDetalle;
	}

	public void setVisibilidadDetalle(String visibilidadDetalle) {
		this.visibilidadDetalle = visibilidadDetalle;
	}
	
	
}
