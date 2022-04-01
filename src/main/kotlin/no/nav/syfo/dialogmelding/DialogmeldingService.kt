package no.nav.syfo.dialogmelding

import no.nav.syfo.dialogmelding.model.OpprettDialogmeldingRequest
import no.nav.syfo.mq.MQSender

class DialogmeldingService(private val mqSender: MQSender) {

    fun opprettDialogmelding(request: OpprettDialogmeldingRequest) {
        // TODO: Implement sending dialogmelding to padm2
        mqSender.send("")
    }
}
