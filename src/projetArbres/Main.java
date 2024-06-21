package projetArbres;

import java.util.ArrayList;
import java.util.Collections;


public class Main {
	
	public static void main(String args[]) {
		double i = 0;
		ArrayList<Double> nbElement = new ArrayList<>();
		ARN<Integer> arnPire ;
		ArrayList<Long> tempsArnConstruction = new ArrayList<>();
		ArrayList<Long> tempsArnConstructionPire = new ArrayList<>();
		ArrayList<Long> tempsArnRecherche = new ArrayList<>();
		ArrayList<Long> tempsArnRecherchePire = new ArrayList<>();


		ArrayList<Long> tempsAbrConstruction = new ArrayList<>();
		ArrayList<Long> tempsAbrConstructionPire = new ArrayList<>();
		ArrayList<Long> tempsAbrRecherche = new ArrayList<>();
		ArrayList<Long> tempsAbrRecherchePire = new ArrayList<>();

		
		while(i < 100001) {
			ArrayList<Integer> array = new ArrayList<>();
			ArrayList<Integer> arrayNotContained = new ArrayList<>();
			long totalConstruction = 0;
			long totalRecherche = 0;
			
			for(int j = 0; j < i; j++) {
				array.add(j);
			}
			for(int j = (int)i; j < i*2; j++) {
				arrayNotContained.add(j);
			}
			
			Collections.shuffle(array);
			
		
			
			totalConstruction = 0;
			totalRecherche = 0;
			for(int j = 0; j < 10 ; j++) {
				long  debutConstruction = System.currentTimeMillis();
				ARN<Integer> arn = new ARN<>(array);
				long  finConstruction = System.currentTimeMillis();
				totalConstruction += finConstruction - debutConstruction;
				
				for(Integer e: array) {
					long  debutRecherche = System.currentTimeMillis();
					arn.contains(e);
					long  finRecherche = System.currentTimeMillis();
					totalRecherche += finRecherche - debutRecherche;
				}
				for(Integer e: arrayNotContained) {
					long  debutRecherche = System.currentTimeMillis();
					arn.contains(e);
					long  finRecherche = System.currentTimeMillis();
					totalRecherche += finRecherche - debutRecherche;
				}
			}
			tempsArnConstruction.add(totalConstruction/10);
			tempsArnRecherche.add(totalRecherche/10);

			Collections.sort(array);
			
			totalConstruction = 0;
			totalRecherche = 0;
			
			
			totalConstruction = 0;
			totalRecherche = 0;
			for(int j= 0; j<10; j++) {
				long  debutConstruction = System.currentTimeMillis();
				arnPire = new ARN<>(array);
				long  finConstruction = System.currentTimeMillis();
				totalConstruction += finConstruction - debutConstruction;
				
				for(Integer e: array) {
					long  debutRecherche = System.currentTimeMillis();
					arnPire.contains(e);
					long  finRecherche = System.currentTimeMillis();
					totalRecherche += finRecherche - debutRecherche;
				}
				for(Integer e: arrayNotContained) {
					long  debutRecherche = System.currentTimeMillis();
					arnPire.contains(e);
					long  finRecherche = System.currentTimeMillis();
					totalRecherche += finRecherche - debutRecherche;
				}
			}
			tempsArnConstructionPire.add(totalConstruction/10);
			tempsArnRecherchePire.add((totalRecherche/10));

			System.out.println("i:" + i + " t: "+ totalConstruction/10 + " " +(double)(totalConstruction/10));
			nbElement.add(i);
			i+=5000;
		}
		
		
		System.out.println("tempsArnConstruction");

		for(Long e: tempsArnConstruction) {
			System.out.println(e);
		}
		System.out.println("tempsArnConstructionPire");

		for(Long e: tempsArnConstructionPire) {
			System.out.println(e);
		}
		System.out.println("tempsArnRecherche");

		for(Long e: tempsArnRecherche) {
			System.out.println(e);
		}
		System.out.println("tempsArnRecherchePire");

		for(Long e: tempsArnRecherchePire) {
			System.out.println(e);
		}
		System.out.println("tempsAbrConstruction");
		System.out.println(arnPire.racine.pere);

		
	}
}
