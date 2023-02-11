package me.theclashfruit.rithle.classes

import android.net.Uri
import io.noties.markwon.image.ImageItem
import io.noties.markwon.image.SchemeHandler

abstract class ProxySchemeHandler : SchemeHandler() {
    abstract override fun handle(raw: String, uri: Uri): ImageItem
    abstract override fun supportedSchemes(): Collection<String?>
}
