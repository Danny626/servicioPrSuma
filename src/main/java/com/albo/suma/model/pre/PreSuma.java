package com.albo.suma.model.pre;

import java.util.ArrayList;

import com.albo.suma.model.AlmRec;
import com.albo.suma.model.ArrMer;
import com.albo.suma.model.ConTotSobFal;
import com.albo.suma.model.InfTec;
import com.albo.suma.model.IngUbiMer;

public class PreSuma {
	public String id;
	public String num;
	public int ver;
	public long fecTra;
	public String estAct;
	public long fecEstAct;
	public String cor;
	public DatGen datGen;
	public DstPre dst;
	public NotA notA;
	public TraEmi traEmi;
	public TraPro traPro;
	public ArrMer arrMer;
	public IngUbiMer ingUbiMer;
	public ArrayList<DetIte> detIte;
	public ArrayList<AlmRec> almRec;
	public ConTotSobFal conTotSobFal;
	public InfTec infTec;
	public boolean prmAgr;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public int getVer() {
		return ver;
	}

	public void setVer(int ver) {
		this.ver = ver;
	}

	public long getFecTra() {
		return fecTra;
	}

	public void setFecTra(long fecTra) {
		this.fecTra = fecTra;
	}

	public String getEstAct() {
		return estAct;
	}

	public void setEstAct(String estAct) {
		this.estAct = estAct;
	}

	public long getFecEstAct() {
		return fecEstAct;
	}

	public void setFecEstAct(long fecEstAct) {
		this.fecEstAct = fecEstAct;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public DatGen getDatGen() {
		return datGen;
	}

	public void setDatGen(DatGen datGen) {
		this.datGen = datGen;
	}

	public DstPre getDst() {
		return dst;
	}

	public void setDst(DstPre dst) {
		this.dst = dst;
	}

	public NotA getNotA() {
		return notA;
	}

	public void setNotA(NotA notA) {
		this.notA = notA;
	}

	public TraEmi getTraEmi() {
		return traEmi;
	}

	public void setTraEmi(TraEmi traEmi) {
		this.traEmi = traEmi;
	}

	public TraPro getTraPro() {
		return traPro;
	}

	public void setTraPro(TraPro traPro) {
		this.traPro = traPro;
	}

	public ArrMer getArrMer() {
		return arrMer;
	}

	public void setArrMer(ArrMer arrMer) {
		this.arrMer = arrMer;
	}

	public IngUbiMer getIngUbiMer() {
		return ingUbiMer;
	}

	public void setIngUbiMer(IngUbiMer ingUbiMer) {
		this.ingUbiMer = ingUbiMer;
	}

	public ArrayList<DetIte> getDetIte() {
		return detIte;
	}

	public void setDetIte(ArrayList<DetIte> detIte) {
		this.detIte = detIte;
	}

	public ArrayList<AlmRec> getAlmRec() {
		return almRec;
	}

	public void setAlmRec(ArrayList<AlmRec> almRec) {
		this.almRec = almRec;
	}

	public ConTotSobFal getConTotSobFal() {
		return conTotSobFal;
	}

	public void setConTotSobFal(ConTotSobFal conTotSobFal) {
		this.conTotSobFal = conTotSobFal;
	}

	public InfTec getInfTec() {
		return infTec;
	}

	public void setInfTec(InfTec infTec) {
		this.infTec = infTec;
	}

	public boolean isPrmAgr() {
		return prmAgr;
	}

	public void setPrmAgr(boolean prmAgr) {
		this.prmAgr = prmAgr;
	}

}
