package signal

import grails.compiler.GrailsCompileStatic
import grails.web.api.WebAttributes
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.ui.dsl.UiMenuSpecifier
import taack.app.TaackApp
import taack.app.TaackAppRegisterService

import javax.annotation.PostConstruct

@GrailsCompileStatic
class SignalUiService implements WebAttributes {

    static lazyInit = false

    @PostConstruct
    void init() {
        TaackAppRegisterService.register(new TaackApp(SignalController.&index as MC, new String(this.class.getResourceAsStream("/signal/signal.svg").readAllBytes())))        
    }


    UiMenuSpecifier buildMenu() {
        UiMenuSpecifier m = new UiMenuSpecifier()
        m.ui {
            menu SignalController.&index as MC
        }
        m
    }
}

