package main.model.utils.services.tabstrategy;

import main.view.View;

import java.util.Set;


public interface TabShowStrategy {
    Set<String> tabNames();

    default void fillTabs(View view) {
        view.update();
    }
}
