package com.ism.views;

import com.ism.data.entities.Article;

public interface IArticleView extends IView<Article> {
    Object checked(String msg, String msgError);
}
