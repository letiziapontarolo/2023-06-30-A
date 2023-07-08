package it.polito.tdp.exam.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.exam.db.BaseballDAO;

public class Model {
	
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private BaseballDAO dao;
	private Map<String, Team> teamIdMap;
	private List<Integer> listaAnni;
	
	public Model() {
		
		dao = new BaseballDAO();
		teamIdMap = new HashMap<String, Team>();
		
		
	}
	
	public String viciniVertice(int anno) {
		
		String finale = "";
		List<Integer> vicini = Graphs.neighborListOf(this.grafo, anno);
		List<AnnoConPeso> listaAnniConPeso = new ArrayList<>();
		
		for (int n : vicini) {
			DefaultWeightedEdge e = grafo.getEdge(anno, n);
			AnnoConPeso a1 = new AnnoConPeso(n, grafo.getEdgeWeight(e));
			listaAnniConPeso.add(a1);
			
		}
		Collections.sort(listaAnniConPeso, new Comparator<AnnoConPeso>() {
		    @Override
		    public int compare(AnnoConPeso p1, AnnoConPeso p2) {
		        return (int) (p1.getPeso() - p2.getPeso());
		    }
		});
		Collections.reverse(listaAnniConPeso);
		
		for (AnnoConPeso ap : listaAnniConPeso) {
		finale = finale + anno + "<->anno: " + ap.getAnno() + "; peso: " +  ap.getPeso() +"\n";
		}
		return finale;
	}
	
	
	public Set<Integer> listaAnni() {
	
		return this.grafo.vertexSet();
				
	}
	
	public List<String> listaTeam() {
		
		
		List<String> lista = new ArrayList<>();
		for (Team t: this.dao.readAllTeams()) {
			lista.add(t.getName());
			teamIdMap.put(t.getTeamCode(), t);
			
		}
		return lista;
	}
	
	public void creaGrafo(String nome) {
		
		
		listaAnni = new ArrayList<>();
		grafo = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo, this.dao.readYear(nome));
		listaAnni.addAll(this.dao.readYear(nome));
		for (Adiacenza a: this.dao.readAdiacenze(nome)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getY1(), a.getY2(), 0);
		}
		
		for (Adiacenza a: this.dao.readAdiacenzePeso(nome)) {
			DefaultWeightedEdge e = grafo.getEdge(a.getY1(), a.getY2());
			if (this.grafo.containsEdge(e)) {
				this.grafo.setEdgeWeight(a.getY1(), a.getY2(), a.getPeso());
			}
	
			}
		
		
		
		
	}
	
	public int numeroVertici() {
		return this.grafo.vertexSet().size();
		}
		 public int numeroArchi() {
		return this.grafo.edgeSet().size();
		}

	
}


