package main.model.utils.services.tabstrategy;

import java.util.Set;


public class UserStrategy implements TabShowStrategy {
    @Override
    public Set<String> tabNames() {
        return Set.of("opTab");
    }

}
