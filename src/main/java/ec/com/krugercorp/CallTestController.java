package ec.com.krugercorp;


import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;




@Controller("/api")
public class CallTestController {
    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    // test
    private String variable;



    @Get("/helloWord")
    public String index() {
        variable = "eu";
        return "hOLA MUNDO que hay de nuevo, Esta es una prueba de despliegue continuo" ;
    }


    @Get("/persona")
    public String pesona() {
        String result = "{'id':'1','nombre':'Christian', 'apellido':'Gudiño', 'mail':'cgudino@ec.krugercorp.com'}";
        System.err.println("prueba de log");

        return result;
    }


    @Get("/suma")
    public Integer suma(Integer val1, Integer val2) {
        Integer res = val1 + val2;
        return res;
    }


}