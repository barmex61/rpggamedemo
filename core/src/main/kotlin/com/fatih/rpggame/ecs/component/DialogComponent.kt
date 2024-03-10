package com.fatih.rpggame.ecs.component

import com.fatih.rpggame.dialog.Dialog
import com.github.quillraven.fleks.Entity

enum class DialogId{
    NONE,
    BLOB
}

class DialogComponent {
    var dialogId : DialogId = DialogId.NONE
    var currentDialog : Dialog? = null
    var interractEntity : Entity? = null
}


