package projetArbres;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ARN<E> extends AbstractCollection<E> {
	
	private Noeud racine;
	private Comparator<? super E> cmp;
	private int taille; 
	Noeud sentinelle;

	private class Noeud {
		E cle;
		Noeud gauche;
		Noeud droit;
		Noeud pere;
		char couleur;
		
		Noeud(E cle) {
			this.cle = cle;
			this.gauche = null;
			this.droit = null;
			this.pere = null;
			this.couleur = 'R';
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
			while (tmp.gauche != sentinelle) {
				tmp = tmp.gauche;
			}
			return tmp;
		}
		
		@Override
		public boolean equals(Object o) {
			
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Noeud noeud = (Noeud) o;
			return cle.equals(noeud.cle);
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

			if(tmp.droit != sentinelle) {
				return tmp.droit.minimum();
			}
			while(tmp.pere != null  && tmp == tmpPere.droit) {
				tmp = tmpPere;
				tmpPere = tmpPere.pere;
			}			
			return tmpPere;
		}
	}
	
	private Noeud sentinelle(){
		Noeud sentinelle = new Noeud(null);
		sentinelle.pere = sentinelle.gauche = sentinelle.droit = sentinelle;
		sentinelle.couleur = 'N';
		return sentinelle;
	}
	
	public ARN() {
		this.cmp = (e1, e2) -> ((Comparable<E>)e1).compareTo(e2);
		this.sentinelle =  sentinelle();
		this.taille = 0;
		this.racine = sentinelle;
	}
	
	public ARN(Comparator<? super E> cmp) {
	    this.taille = 0;
	    this.cmp = cmp;
	    this.sentinelle = sentinelle();
		this.racine = sentinelle;
	}

	/**
	 * Constructeur par recopie. Crée un arbre qui contient les mêmes éléments
	 * que c. L'ordre des éléments est l'ordre naturel.
	 * 
	 * @param c
	 *            la collection à copier
	 */
	public ARN(Collection<? extends E> c) {
		this.taille = 0;
		this.cmp = (e1, e2) -> ((Comparable<E>)e1).compareTo(e2);       
	    this.sentinelle = sentinelle();
	    this.racine = sentinelle;
		addAll(c);
		
	}

	public Iterator<E> iterator() {
		return new ARNIterator();
	}
	
	private class ARNIterator implements Iterator<E> {
		Noeud tmp;
		Noeud suivant;

		public ARNIterator(){
			this.tmp = ARN.this.sentinelle;
			this.suivant = ARN.this.racine.minimum();
		}
	
		public boolean hasNext() {
			return suivant != ARN.this.sentinelle;
		}
	
		public E next() {
			this.tmp = this.suivant;
			this.suivant = this.suivant.suivant();
			return this.tmp.cle;
		}
	
		public void remove() {
			this.suivant = ARN.this.supprimer(this.tmp);
		}
	
	}	
	
	
	public int size() {
		return taille;
	}
	
	private Noeud rechercher(Object o) {
		Noeud tmp = racine;
		while(tmp != sentinelle && tmp.cle != (E)o){
			if(cmp.compare(tmp.cle, (E)o) < 0) {
				tmp = tmp.droit;
			}
			else {
				tmp = tmp.gauche;
			}
		}
		if(tmp == sentinelle) {
			return null;
		}
		return tmp;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		toString(racine, buf, "", maxStrLen(racine));
		return buf.toString();
	}

	private void toString(Noeud x, StringBuffer buf, String path, int len) {
		if (x == sentinelle)
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
		if (x.couleur == 'R') {
			buf.append("-- " + "\u001B[31m" + x.cle.toString() + "\u001B[0m");
		} else {
			buf.append("-- " + x.cle.toString());
		}
		if (x.gauche != sentinelle || x.droit != sentinelle) {
			buf.append(" --");
			for (int j = x.cle.toString().length(); j < len; j++)
				buf.append('-');
			buf.append('|');
		}
		buf.append("\n");
		toString(x.gauche, buf, path + "G", len);
	}

	private int maxStrLen(Noeud x) {
		return x == sentinelle ? 0 : Math.max(x.cle.toString().length(),
			Math.max(maxStrLen(x.gauche), maxStrLen(x.droit)));
	}
	
	private void ajouterCorrection(Noeud z) {
		Noeud y;
		while (z.pere.couleur == 'R') {
			if (z.pere == z.pere.pere.gauche) {
				y = z.pere.pere.droit; // l'oncle de z
				if (y.couleur == 'R') {
					// cas 1
					z.pere.couleur = 'N';
					y.couleur = 'N';
					z.pere.pere.couleur = 'R';
					z = z.pere.pere;
				} else {
					if (z == z.pere.droit) {
						// cas 2
						z = z.pere;
						rotationGauche(z);
					}
					// cas 3
					z.pere.couleur = 'N';
					z.pere.pere.couleur = 'R';
					rotationDroite(z.pere.pere);
				}
			} else {
				y = z.pere.pere.gauche; // l'oncle de z
				if (y.couleur == 'R') {
					// cas 1
					z.pere.couleur = 'N';
					y.couleur = 'N';
					z.pere.pere.couleur = 'R';
					z = z.pere.pere;
				} else {
					if (z == z.pere.gauche) {
						// cas 2
						z = z.pere;
						rotationDroite(z);
					}
					// cas 3
					z.pere.couleur = 'N';
					z.pere.pere.couleur = 'R';
					rotationGauche(z.pere.pere);
				}
			}
		}
		racine.couleur = 'N';
	}
	
	private Noeud supprimer(Noeud z) {
		Noeud x;
		Noeud y;
		if(z.droit == sentinelle || z.gauche == sentinelle) {
			y = z;
		}
		else {
			y = z.suivant();

		}
		
		if(y.gauche != sentinelle) {
			x = y.gauche;
		}
		else {
			x = y.droit;

		}

		x.pere = y.pere;
		if(y.pere == sentinelle) {
			racine = x;
		}
		else {
			if(y == y.pere.gauche) {
				y.pere.gauche = x;
			}
			else {
				y.pere.droit = x;
			}
		}

		if(y !=z) z.cle = y.cle;
		if(y.couleur == 'N') supprimerCorrection(x);
		taille --;
		return y;
	}
	
	private void supprimerCorrection(Noeud x) {
		Noeud w;
		while(x != racine && x.couleur == 'N') {
			if(x == x.pere.gauche) {
				w = x.pere.droit;
				if(w.couleur == 'R') {
					w.couleur = 'N';
					x.pere.couleur ='R';
					rotationGauche(x.pere);
					w = x.pere.droit;
				}
				if(w.gauche.couleur == 'N' && w.droit.couleur == 'N') {
					w.couleur = 'R';
					x = x.pere;
				}
				else {
					if(w.droit.couleur == 'N') {
						w.gauche.couleur = 'N';
						w.couleur = 'R';
						rotationDroite(w);
						w = x.pere.droit;
					}
					w.couleur = x.pere.couleur;
					x.pere.couleur = 'N';
					w.droit.couleur = 'N';
					rotationGauche(x.pere);
					x = racine;
				}
			}
			else {
				w = x.pere.gauche; 
				if (w.couleur == 'R') {
					w.couleur = 'N';
				    x.pere.couleur = 'R';
				    rotationDroite(x.pere);
				    w = x.pere.gauche;
				}
				if (w.droit.couleur == 'N' && w.gauche.couleur == 'N') {
					w.couleur = 'R';
				    x = x.pere;
				} 
				else {
					if (w.gauche.couleur == 'N') {
						w.droit.couleur = 'N';
						w.couleur = 'R';
						rotationGauche(w);
						w = x.pere.gauche;
					}
					w.couleur = x.pere.couleur;
					x.pere.couleur = 'N';
					w.gauche.couleur = 'N';
					rotationDroite(x.pere);
					x = racine;
				}
			}
		}
		x.couleur = 'N';
	}

	private void rotationGauche(Noeud x) {
		Noeud y = x.droit;
		x.droit = y.gauche;
		if(y.gauche != sentinelle){
			y.gauche.pere = x;
		}
		y.pere = x.pere;

		if(x.pere == sentinelle){
			this.racine = y;
		}
		else if(x == x.pere.gauche){
			x.pere.gauche = y;
		}
		else {
			x.pere.droit = y;
		}
		y.gauche = x;
		x.pere = y;
	}
	
	private  void rotationDroite(Noeud x){
		Noeud y = x.gauche;
		x.gauche = y.droit;
		if(y.droit !=sentinelle){
			y.droit.pere = x;
		}
		y.pere = x.pere;
		if (x.pere == sentinelle){
			this.racine = y;
		}
		else if (x == x.pere.droit){
			x.pere.droit = y;
		}
		else {
			x.pere.gauche = y;
		}
		y.droit = x;
		x.pere = y;
	}

	public  Comparator<? super E> getCmp() {
		return cmp;
	}
	// TODO : voir quelles autres méthodes il faut surcharger
	
	@Override
	public boolean add(E e) {
		if(e == null) {
			return false;
		}		
		Noeud nouveau = new Noeud(e);
		Noeud tmp = racine;
		Noeud pereTmp = sentinelle;
		
		while(tmp != sentinelle) {
			pereTmp = tmp;
			if(pereTmp.equals(nouveau)) { //pour ne pas ajouter une clé déjà présente dans l'arbre
				return false;
			}
			tmp = this.cmp.compare(nouveau.cle, tmp.cle) < 0 ? tmp.gauche : tmp.droit;
		}
		if(pereTmp == sentinelle) {
			this.racine = nouveau;
		}
		else { 
			if(this.cmp.compare(nouveau.cle, pereTmp.cle) < 0) {
			
			pereTmp.gauche = nouveau;
			}
			else {
			pereTmp.droit = nouveau;
			}
		}
		nouveau.pere = pereTmp;
		nouveau.gauche = nouveau.droit = sentinelle;
		ajouterCorrection(nouveau);
		this.taille = taille+1;
		return true;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)		//ajoute toute la collection a l'arbre, retourne vrai si c'est le cas
	{
		boolean toutAjoute = true;
		for( E e : c )
			if(!add(e)) toutAjoute = false;

		return toutAjoute;
	}
	
	@Override
	public boolean contains(Object cle) {	//retourne vrai si la clé est contenue dans l'arbre 
		if(cle == null) return false;
		return rechercher(cle) != null;
	}
	
	@Override
	public boolean isEmpty() {			//retourne vrai si l'arbre est vide
		return racine == sentinelle;
	}
	
	@Override
	public void clear() {					//vide l'arbre
		racine = sentinelle;
	}
	
	@Override
	public boolean remove(Object o) {		//retire le noeud contenant la clé passée en pramètre
		Noeud aSup = rechercher(o);
		if(aSup == null) return false;
		supprimer(aSup);
		return true;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) { //enlève tous les noeud contenant les clé de la collection
		boolean retour = true;
		for(Object e: c) {
			if(remove(e)) {
				retour = false;
			}
		}
		return retour;
	}
	
	
	public static void main(String args[]) {
		ARN<Integer> arn = new ARN<>();
		ArrayList<Integer> array = new ArrayList<>();
		array.add(25);
		array.add(12);
		array.add(50);
		array.add(8);
		array.add(20);
		array.add(1);
		array.add(5);
		array.add(3);
		array.add(2);
		array.add(4);
		array.add(20);
		array.add(25);
		array.add(38);
		array.add(90);
		array.add(45);
		array.add(44);
		array.add(22);
		array.add(35);

		arn = new ARN<>(array);
		System.out.println(arn);


		
	}
}