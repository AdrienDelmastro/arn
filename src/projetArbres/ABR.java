package projetArbres;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;



public class ABR<E> extends AbstractCollection<E> {
	private Noeud racine;
	private int taille;
	private Comparator<? super E> cmp;

	private class Noeud {
		E cle;
		Noeud gauche;
		Noeud droit;
		Noeud pere;

		Noeud(E cle) {
			this.droit = null;
			this.gauche = null;
			this.pere = null;
			this.cle = cle;
		}

		/**
		 * Renvoie le noeud contenant la clé minimale du sous-arbre enraciné
		 * dans ce noeud
		 * 
		 * @return le noeud contenant la clé minimale du sous-arbre enraciné
		 *         dans ce noeud
		 */
		Noeud minimum() {
			Noeud tmp = this;
			while(tmp.gauche != null) {
				tmp = tmp.gauche;
			}
			return tmp;		
		}

		/**
		 * Renvoie le successeur de ce noeud
		 * 
		 * @return le noeud contenant la clé qui suit la clé de ce noeud dans
		 *         l'ordre des clés, null si c'es le noeud contenant la plus
		 *         grande clé
		 */
		Noeud suivant() {
			Noeud tmp = this;
			Noeud tmpPere = tmp.pere;

			if(tmp.droit != null) {
				return tmp.droit.minimum();
			}
			while(tmpPere != null && tmp == tmpPere.droit) {
				tmp = tmpPere;
				tmpPere = tmpPere.pere;
			}			
			return tmpPere;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Noeud noeud = (Noeud) o;
			return cle.equals(noeud.cle);
		}
	}

	// Consructeurs

	/**
	 * Crée un arbre vide. Les éléments sont ordonnés selon l'ordre naturel
	 */
	public ABR() {
		cmp = (e1, e2) -> ((Comparable<E>)e1).compareTo(e2);       
	    this.racine = null;
	    this.taille = 0;
	}

	/**
	 * Crée un arbre vide. Les éléments sont comparés selon l'ordre imposé par
	 * le comparateur
	 * 
	 * @param cmp
	 *            le comparateur utilisé pour définir l'ordre des éléments
	 */
	public ABR(Comparator<? super E> cmp) {
		this.racine = null;
	    this.taille = 0;
	    this.cmp = cmp;
	}

	/**
	 * Constructeur par recopie. Crée un arbre qui contient les mêmes éléments
	 * que c. L'ordre des éléments est l'ordre naturel.
	 * 
	 * @param c
	 *            la collection à copier
	 */
	public ABR(Collection<? extends E> c) {
		taille = 0;
		racine = null;
		cmp = (e1, e2) -> ((Comparable<E>)e1).compareTo(e2);       
		addAll(c);
		
	}

	@Override
	public Iterator<E> iterator() {
		return new ABRIterator();
	}

	@Override
	public int size() {
		return this.taille;
	}

	// Quelques méthodes utiles

	/**
	 * Recherche une clé. Cette méthode peut être utilisée par
	 * {@link #contains(Object)} et {@link #remove(Object)}
	 * 
	 * @param o
	 *            la clé à chercher
	 * @return le noeud qui contient la clé ou null si la clé n'est pas trouvée.
	 */
	private Noeud rechercher(Object o) {
		Noeud tmp = racine;
		while(tmp != null && tmp.cle != (E)o){
			if(cmp.compare(tmp.cle, (E)o) < 0) {
				tmp = tmp.droit;
			}
			else {
				tmp = tmp.gauche;
			}
		}
		return tmp;
	}

	/**
	 * Supprime le noeud z. Cette méthode peut être utilisée dans
	 * {@link #remove(Object)} et {@link Iterator#remove()}
	 * 
	 * @param z
	 *            le noeud à supprimer
	 * @return le noeud contenant la clé qui suit celle de z dans l'ordre des
	 *         clés. Cette valeur de retour peut être utile dans
	 *         {@link Iterator#remove()}
	 */
	
	private Noeud supprimer(Noeud z) {
		Noeud x;
		Noeud y = z.pere;
		  if (z.gauche == null || z.droit == null)
		    y = z;
		  else
		    y = z.suivant();
		  // y est le nœud à détacher

		  if (y.gauche != null)
		    x = y.gauche;
		  else
		    x = y.droit;
		  // x est le fils unique de y ou null si y n'a pas de fils

		  if (x != null) x.pere = y.pere;

		  if (y.pere == null) { // suppression de la racine
		    racine = x;
		  } else {
		    if (y == y.pere.gauche)
		      y.pere.gauche = x;
		    else
		      y.pere.droit = x;
		  }

		  if (y != z) z.cle = y.cle;
		  taille--;
		  return y;
		}




	/**
	 * Les itérateurs doivent parcourir les éléments dans l'ordre ! Ceci peut se
	 * faire facilement en utilisant {@link Noeud#minimum()} et
	 * {@link Noeud#suivant()}
	 */
	private class ABRIterator implements Iterator<E> {
		Noeud tmp;
		Noeud prev = null;

		 ABRIterator(){
			 if(racine != null) {
				 tmp = racine.minimum();
			 }
		 }
		public boolean hasNext() {
			return tmp != null;
		}

		public E next() {
			E cle = tmp.cle;
			prev = tmp;
			tmp = tmp.suivant();
			return cle;
		}

		public void remove() {
			this.tmp = ABR.this.supprimer(prev);
		}
	}

	// Pour un "joli" affichage

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		toString(racine, buf, "", maxStrLen(racine));
		return buf.toString();
	}

	private void toString(Noeud x, StringBuffer buf, String path, int len) {
		if (x == null)
			return;
		toString(x.droit, buf, path + "D", len);
		for (int i = 0; i < path.length(); i++) {
			for (int j = 0; j < len + 6; j++)
				buf.append(' ');
			char c = ' ';
			if (i == path.length() - 1)
				c = '+';
			else if (path.charAt(i) != path.charAt(i + 1))
				c = '|';
			buf.append(c);
		}
		buf.append("-- " + x.cle.toString());
		if (x.gauche != null || x.droit != null) {
			buf.append(" --");
			for (int j = x.cle.toString().length(); j < len; j++)
				buf.append('-');
			buf.append('|');
		}
		buf.append("\n");
		toString(x.gauche, buf, path + "G", len);
	}

	private int maxStrLen(Noeud x) {
		return x == null ? 0 : Math.max(x.cle.toString().length(),
				Math.max(maxStrLen(x.gauche), maxStrLen(x.droit)));
	}

	// TODO : voir quelles autres méthodes il faut surcharger
	
	
	
	@Override
	public boolean add(E e) {
		if(e == null) {
			return false;
		}
		Noeud nouveau = new Noeud(e);
		if(racine == null) {
			racine = nouveau;
			this.taille = taille+1;
			return true;
		}
		else {
			Noeud tmp = racine;
			Noeud pereTmp = null;
			while(tmp != null) {
				pereTmp = tmp;
				if(pereTmp.equals(nouveau)) {
					return false;
				}
				tmp = this.cmp.compare(nouveau.cle, tmp.cle) < 0 ? tmp.gauche : tmp.droit;
			}
			if(this.cmp.compare(nouveau.cle, pereTmp.cle) < 0) {
				pereTmp.gauche = nouveau;
			}
			else {
				pereTmp.droit = nouveau;
			}
			nouveau.pere = pereTmp;
		}
		this.taille = taille+1;
		return true;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		boolean toutAjoute = true;
		for( E e : c )
			if(!add(e)) toutAjoute = false;

		return toutAjoute;
	}
	
	@Override
	public boolean contains(Object cle) {
		if(cle == null) return false;
		return rechercher(cle) != null;
	}
}