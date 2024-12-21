package com.ism;

import java.util.Scanner;

import com.ism.core.config.router.IRouter;
import com.ism.core.config.router.Router;
import com.ism.core.factory.IFactory;
import com.ism.core.factory.implement.Factory;
import com.ism.views.implement.ImpView;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ImpView.setScanner(scanner);
        IFactory factory = Factory.getInstance();
        IRouter router = new Router(factory, scanner);

        router.navigate();
    }
}