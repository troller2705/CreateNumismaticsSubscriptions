package com.troller2705.numismatics_subscriptions;

import com.simibubi.create.Create;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.ithundxr.createnumismatics.Numismatics;

public class AllPartialModels {


    public static final PartialModel MONOCLE = entity("monocle");


    private static PartialModel createBlock(String path) {
        return PartialModel.of(Create.asResource("block/" + path));
    }

    private static PartialModel block(String path) { return PartialModel.of(NumismaticsSubscriptions.asResource("block/" + path)); }

    private static PartialModel entity(String path) { return PartialModel.of(NumismaticsSubscriptions.asResource("entity/" + path)); }

    public static void initialize(){}
}
