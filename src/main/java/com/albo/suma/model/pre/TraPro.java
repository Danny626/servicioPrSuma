package com.albo.suma.model.pre;

import java.util.ArrayList;

import com.albo.suma.model.Car;
import com.albo.suma.model.TipDoc;
import com.albo.suma.model.TipOpe;
import com.albo.suma.model.TipSol;

public class TraPro {
	public boolean oea;
	public boolean oac;
	public Car car;
	public TipDoc tipDoc;
	public String numDoc;
	public String nomRazSoc;
	public String cat;
	public ArrayList<TipOpe> tipOpe;
	public ArrayList<Object> marOpe;
	public TipSol tipSol;
	public DomPre dom;

	public boolean isOea() {
		return oea;
	}

	public void setOea(boolean oea) {
		this.oea = oea;
	}

	public boolean isOac() {
		return oac;
	}

	public void setOac(boolean oac) {
		this.oac = oac;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public TipDoc getTipDoc() {
		return tipDoc;
	}

	public void setTipDoc(TipDoc tipDoc) {
		this.tipDoc = tipDoc;
	}

	public String getNumDoc() {
		return numDoc;
	}

	public void setNumDoc(String numDoc) {
		this.numDoc = numDoc;
	}

	public String getNomRazSoc() {
		return nomRazSoc;
	}

	public void setNomRazSoc(String nomRazSoc) {
		this.nomRazSoc = nomRazSoc;
	}

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
	}

	public ArrayList<TipOpe> getTipOpe() {
		return tipOpe;
	}

	public void setTipOpe(ArrayList<TipOpe> tipOpe) {
		this.tipOpe = tipOpe;
	}

	public ArrayList<Object> getMarOpe() {
		return marOpe;
	}

	public void setMarOpe(ArrayList<Object> marOpe) {
		this.marOpe = marOpe;
	}

	public TipSol getTipSol() {
		return tipSol;
	}

	public void setTipSol(TipSol tipSol) {
		this.tipSol = tipSol;
	}

	public DomPre getDom() {
		return dom;
	}

	public void setDom(DomPre dom) {
		this.dom = dom;
	}

}
