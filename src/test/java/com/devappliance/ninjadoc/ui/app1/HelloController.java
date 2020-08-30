
package com.devappliance.ninjadoc.ui.app1;

import ninja.Result;
import ninja.Results;
import ninja.params.Param;

import javax.validation.Valid;
import javax.validation.constraints.Size;

public class HelloController {

    public Result persons(@Valid @Param("name") @Size(min = 4, max = 6) String name) {
        return Results.json();
    }

}
