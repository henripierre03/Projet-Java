package com.ism.views.implement;

import java.util.List;

import com.ism.core.helper.Helper;
import com.ism.data.entities.Article;
import com.ism.services.IArticleService;
import com.ism.views.IArticleView;

public class ArticleView extends ImpView<Article> implements IArticleView {
    private IArticleService articleService;

    public ArticleView(IArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public Article saisir() {
        Article article = new Article();
        article.setLibelle(checkLibelle());
        article.setPrix(Double.valueOf(checked("Entrez le prix de l'article : ", "le prix").toString()));
        article.setQteStock(Integer.valueOf(checked("Entrez la quantité de l'article : ", "la quantité").toString()));
        return article;
    }

    private String checkLibelle() {
        String libelle;
        do {
            System.out.print("Entrez le libelle de l'article (minimum 3 caractères, maximum 50 caractères) : ");
            libelle = scanner.nextLine();
            if (libelle.length() < 3 || libelle.length() > 50) {
                System.out.println("Erreur, le libelle doit comporter entre 3 et 50 caractères.");
            }
        } while (libelle.length() < 3 || libelle.length() > 50);
        return Helper.capitalize(libelle);
    }

    @Override
    public Integer checked(String msg, String msgError) {
        String value;
        String error = "Erreur, ";
        boolean valid = false; // Pour contrôler la boucle
        Integer intValue = null;
    
        do {
            System.out.print(msg);
            value = scanner.nextLine();
    
            try {
                intValue = Integer.parseInt(value); // Essaie de convertir la valeur en entier
                if (intValue == 0) {
                    System.out.println("Erreur, la valeur ne peut pas être 0.");
                } else if (intValue < 0) {
                    System.out.println(error + msgError + " ne peut être négatif.");
                } else if (!isDecimal(value) && msgError.contains("prix")) {
                    System.out.println(error + msgError + " doit être un nombre décimal.");
                } else if (!isInteger(value) && msgError.contains("quantité")) {
                    System.out.println(error + msgError + " doit être un nombre entier.");
                } else {
                    valid = true; // Tout est correct, sortir de la boucle
                }
            } catch (NumberFormatException e) {
                System.out.println("Erreur, veuillez entrer un nombre valide.");
            }
    
        } while (!valid);
    
        return intValue;
    }

    @Override
    public Article getObject(List<Article> articles) {
        Article article;
        String choix;
        this.afficher(articles);
        do {
            article = new Article();
            System.out.print("Choisissez un article par son id: ");
            choix = scanner.nextLine();
            if (isInteger(choix)) {
                article.setId(Long.parseLong(choix));
                article = articleService.findBy(article, articles);
            } else {
                continue;
            }
            System.out.println(article);
            if (article == null) {
                System.out.println("Erreur, l'id de l'article est invalide.");
            }
        } while (article == null);
        return article;
    }
}
