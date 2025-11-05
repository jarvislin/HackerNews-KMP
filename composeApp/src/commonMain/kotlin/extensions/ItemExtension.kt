package extensions

import domain.models.Item
import domain.models.getTitle
import domain.models.getUrl


private const val SHARE_BASE_URL = "https://news.ycombinator.com/item?id="

fun Item.shareLinkText() = "${getTitle()} | ${getUrl()}"

fun Item.shareCommentsText() = "${getTitle()} | $SHARE_BASE_URL${getItemId()}"
