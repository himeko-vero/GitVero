package com.microforum.gestorencuestas.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="TRABAJO_ADMINISTRACION_SISTEMA")
public class Trabajo {
	@Id
	@Column(name="NUMERO_REFERENCIA")
	@GenericGenerator(name="UUID-GENERATOR", strategy="uuid")
	@GeneratedValue(generator="UUID-GENERATOR")
	private String numReferencia;
	@Column(name="DESCRIPCION_TRABAJO")
	private String Descripcion;
	@Column(name="FECHA_TRABAJO")
	@Temporal(TemporalType.DATE)
	private Date fecha;
	@ManyToMany(mappedBy="trabajos")
	private Collection<Administrador> autores=new ArrayList<Administrador>(); //Se deja construido para que se pueda utilizar
	
	public Collection<Administrador> getAutores() {
		return autores;
	}
	public void setAutores(Collection<Administrador> autores) {
		this.autores = autores;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
}
