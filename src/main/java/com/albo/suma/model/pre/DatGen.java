package com.albo.suma.model.pre;

import com.albo.suma.model.AduDes;
import com.albo.suma.model.AduRec;
import com.albo.suma.model.ModTra;

public class DatGen {
	public String idMan;
	public String idDocEmb;
	public String numMan;
	public String numDocEmb;
	public long fecIng;
	public AduRec aduRec;
	public ConCns con;
	public ModTra modTra;
	public String numTraDE;
	public String tipPre;
	public boolean cos;
	public AduDes aduDes;
	public TipGuiAer tipGuiAer;

	public String getIdMan() {
		return idMan;
	}

	public void setIdMan(String idMan) {
		this.idMan = idMan;
	}

	public String getIdDocEmb() {
		return idDocEmb;
	}

	public void setIdDocEmb(String idDocEmb) {
		this.idDocEmb = idDocEmb;
	}

	public String getNumMan() {
		return numMan;
	}

	public void setNumMan(String numMan) {
		this.numMan = numMan;
	}

	public String getNumDocEmb() {
		return numDocEmb;
	}

	public void setNumDocEmb(String numDocEmb) {
		this.numDocEmb = numDocEmb;
	}

	public long getFecIng() {
		return fecIng;
	}

	public void setFecIng(long fecIng) {
		this.fecIng = fecIng;
	}

	public AduRec getAduRec() {
		return aduRec;
	}

	public void setAduRec(AduRec aduRec) {
		this.aduRec = aduRec;
	}

	public ConCns getCon() {
		return con;
	}

	public void setCon(ConCns con) {
		this.con = con;
	}

	public ModTra getModTra() {
		return modTra;
	}

	public void setModTra(ModTra modTra) {
		this.modTra = modTra;
	}

	public String getNumTraDE() {
		return numTraDE;
	}

	public void setNumTraDE(String numTraDE) {
		this.numTraDE = numTraDE;
	}

	public String getTipPre() {
		return tipPre;
	}

	public void setTipPre(String tipPre) {
		this.tipPre = tipPre;
	}

	public boolean isCos() {
		return cos;
	}

	public void setCos(boolean cos) {
		this.cos = cos;
	}

	public AduDes getAduDes() {
		return aduDes;
	}

	public void setAduDes(AduDes aduDes) {
		this.aduDes = aduDes;
	}

	public TipGuiAer getTipGuiAer() {
		return tipGuiAer;
	}

	public void setTipGuiAer(TipGuiAer tipGuiAer) {
		this.tipGuiAer = tipGuiAer;
	}

}
