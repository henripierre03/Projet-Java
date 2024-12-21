package com.ism.views;

import java.util.List;

import com.ism.data.entities.Dette;

public interface IDetteView extends IView<Dette> {
    void display(List<Dette> dettes);
    void displayDette(Dette dette);
    void displayDetail(Dette dette);
    void displayPay(Dette dette);
}
