package signal


import grails.compiler.GrailsCompileStatic
import grails.events.annotation.Subscriber
import grails.plugin.springsecurity.SpringSecurityService
import io.questdb.client.Sender
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.PostDeleteEvent
import org.grails.datastore.mapping.engine.event.PostInsertEvent
import org.grails.datastore.mapping.engine.event.PostUpdateEvent
import taack.ast.type.FieldInfo

@GrailsCompileStatic
class ManageSignalService {

    SpringSecurityService springSecurityService

    enum TimeOfEvent {
        afterInsert, afterUpdate, afterDelete
    }

    final private Map<Class<? extends GormEntity>, ISignal> gormEntityISignalMap = [:]

    void registerGormEvent(Class<? extends GormEntity> entityClass, ISignal commentSpecifier) {
        gormEntityISignalMap[entityClass] = commentSpecifier
    }

    private void recordEvent(AbstractPersistenceEvent event, TimeOfEvent timeOfEvent) {
        if (gormEntityISignalMap.containsKey(event.entityObject.class)) {

            GormEntity entity = event.entityObject as GormEntity
            ISignal iSignal = gormEntityISignalMap[entity.class]

            try (Sender sender = Sender.fromConfig("http::addr=localhost:9000;")) {
                sender.table("signalEvents")
                        .symbol("timeOfEvent", timeOfEvent.toString())
                        .symbol("entityClass", entity.class.simpleName)
                        .longColumn("entityId", entity.ident() as Long)
                        .longColumn("userId", springSecurityService.currentUserId as Long)
                        .stringColumn("i18n", iSignal.i18nLabel(entity))
                FieldInfo[] fieldInfos = iSignal.i18nArgs(entity)
                if (fieldInfos)
                    for (FieldInfo fi in iSignal.i18nArgs(entity)) {
                        if (fi.value)
                            switch (fi.fieldConstraint.field.type) {
                                case Long:
                                    sender.longColumn(fi.fieldName, fi.value as Long)
                                    break
                                default:
                                    sender.stringColumn(fi.fieldName, fi.value.toString())
                            }
                    }
                sender.atNow()
            }
        }
    }

    @Subscriber
    void afterInsert(PostInsertEvent event) {
        recordEvent(event, TimeOfEvent.afterInsert)
    }

    @Subscriber
    void afterUpdate(PostUpdateEvent event) {
        recordEvent(event, TimeOfEvent.afterUpdate)
    }

    @Subscriber
    void afterDelete(PostDeleteEvent event) {
        recordEvent(event, TimeOfEvent.afterDelete)
    }

}
