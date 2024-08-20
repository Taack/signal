package signal

import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo

interface ISignal {
    String i18nLabel(GormEntity entity)

    FieldInfo[] i18nArgs(GormEntity entity)
}