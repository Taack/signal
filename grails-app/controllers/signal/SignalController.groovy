package signal

import grails.web.api.WebAttributes
import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier

@GrailsCompileStatic
@Secured(['ROLE_ADMIN'])
class SignalController implements WebAttributes {
    TaackUiService taackUiService
    SignalUiService signalUiService

    def index() {
        UiBlockSpecifier b = new UiBlockSpecifier().ui {
          custom "Hello World!"
        }
        taackUiService.show(b, signalUiService.buildMenu())
    }
}

