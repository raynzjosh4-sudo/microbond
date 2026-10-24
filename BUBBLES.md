# Microbond Message Bubbles

This document inventories the message bubble types and reusable bubble
components currently present in the Android app source. The main thread
renderer is the `ThreadBubbleType` dispatch in
`app/src/main/java/com/microbond/app/screens/threadscreen/ThreadScreen.kt`.
The event model and typed payloads are defined in
`app/src/main/java/com/microbond/app/core/data_classes_and_enums/dataclasses.kt`.

## How to read this inventory

- **Thread type** means `ThreadScreen` has a `ThreadBubbleType` branch for it.
- **Reusable component** means a Compose bubble component exists, but it is not
  currently wired as a `ThreadBubbleType` branch in the main thread renderer.
- Several thread types share a visual component. In particular, `TEXT` and
  `REPLY` currently both render through `MicroBondTextBubble`; a separate
  `MicroBondReplyBubble` component also exists but is not selected by that
  dispatch.
- Every event also carries common message metadata such as ID, text, time,
  sender, direction, and optional delivery status. Individual types use the
  relevant additional fields listed below.
- These are UI models and demo/sample behaviors. Their presence does not imply
  a backend, real transaction processing, or persisted group records.

## Conversation and media

| Thread type / component | Description | Main content |
| --- | --- | --- |
| `TEXT` — `MicroBondTextBubble` | Standard incoming or outgoing text message. | Text, timestamp, sender and delivery metadata. |
| `REPLY` — `MicroBondTextBubble` in thread | A message marked as a reply in the event model. The current thread renderer displays it using the standard text bubble; quoted-message fields are available on `ThreadEvent`. | Text plus optional `quotedSender`, `quotedText`, `quotedEventId`, and quote accent. |
| `MicroBondReplyBubble` (reusable component) | Dedicated quoted-reply card component. It exists in the component library but is not currently selected by the `REPLY` dispatch branch. | Reply text and quoted-message context. |
| `VOICE` — `MicroBondVoiceBubble` | Voice-note player-style message with duration and sender avatar/initials. | Duration and optional `voiceUri`. |
| `IMAGE` — `MicroBondImageBubble` | Image message with an optional caption; supports one image or a set of images and optional aspect ratio. | Image URI/resource(s), caption, aspect ratio. |
| `MicroBondGroupedMediaBubble` (reusable component) | Compact multi-item media preview with a count overlay and optional caption. | Number of media items and caption. |
| `VIDEO` — `MicroBondVideoBubble` | Video message presentation with duration and optional caption in its model. | Video duration and optional caption. The current thread renderer passes no video painter. |
| `MicroBondAudioBubble` (reusable component) | Audio attachment-style bubble showing an audio title and duration. | Title and duration. |
| `MicroBondDocumentBubble` / `DOCUMENT` | File attachment card showing document details and supporting a URI/file type. | File name, file size, URI, and file type. |
| `MicroBondContactBubble` / `CONTACT` | Shared contact card; the thread can pass one or more contact entries. | Contact names and contact fields supplied in the contact payload. |
| `MicroBondLocationBubble` / `LOCATION` | Location card with place name and optional subtitle. | `locationPlace` and `locationSubtitle`. |
| `MicroBondLinkPreviewBubble` (reusable component) | Link preview card with a title and URL. It is not currently dispatched as a thread event type. | Link title and URL. |
| `MicroBondGifBubble` (reusable component) | GIF-style message card. This component is not currently a thread event type. | Display title. |
| `MicroBondStickerBubble` (reusable component) | Sticker-style message card. This component is not currently a thread event type. | Sticker label. |
| `MicroBondReactionBubble` (reusable component) | Small emoji reaction badge with a count. It exists independently and is not currently dispatched as a thread event type. | Emoji and reaction count. |

## Savings, payments, and group rules

| Thread type / component | Description | Main content |
| --- | --- | --- |
| `DEPOSIT` — `MicroBondDepositBubble` | Deposit/payment update card showing the contributed amount, required amount when applicable, remaining reminder, and date. The current implementation delegates its visual card to `MicroBondPaymentUpdateBubble`; tapping the receipt action opens the deposit receipt UI. | Amounts, payment method, reference, cycle note, verification flag, date, and required/deposited amounts. |
| `DEPOSIT_STATUS` — `MicroBondDepositStatusBubble` | Group-level tally of full deposits, partial deposits, and members who have not deposited. | Counts for full, half, and no deposits. |
| `PAYMENT_UPDATE` — `MicroBondPaymentUpdateBubble` | Reusable payment summary/reminder card showing the deposited amount against the required amount, date, and optional payment phone/method. | Deposited/required amount labels, reminder, date, title, optional phone and payment method. |
| `SAVINGS_RECEIVED` — `MicroBondSavingsReceivedBubble` | Confirmation-style card indicating savings or a group payout was delivered. | Title, subtitle, recipient, status, and footer. |
| `SECURITY_RULES` — `MicroBondSecurityRulesBubble` | Group rules card with a pinned label and rules list. The current thread callbacks for opening all rules or selecting an individual rule are placeholders. | Title, rule strings, pinned label, view-all label. |
| `SECURITY_RULE_UPDATE` — `MicroBondSecurityRuleUpdateBubble` | Announcement card for a group rule change and its approval state. | Rule text, title, status, approval note, approval date. |
| `SECURITY_POLL` — `MicroBondSecurityPollBubble` | Poll focused on a group-security or governance decision. The displayed callback for choosing an option is currently a placeholder. | Question, options, vote count/result text, and replies. |
| `POLL` — `MicroBondPollBubble` | General poll card with options and single- or multiple-choice configuration. | Question, subtitle, option labels, multiple-choice flag, footer. |
| `ACTION_PROMPT` — `MicroBondActionPromptBubble` | Prompt message with an action button. In the current thread, payment-method prompts use it to choose Mobile Money, Card, or Bank transfer for a demo payment flow. | Prompt text and optional action label. |
| `MicroBondTransferBubble` (reusable component) | Wallet-transfer summary showing an amount and note. It is present as a reusable component but is not currently a `ThreadBubbleType` branch. | Amount and note. |

## Reports and chart/statistic bubbles

| Thread type / component | Description | Main content |
| --- | --- | --- |
| `WEEKLY_REPORT` — `MicroBondWeeklyReportBubble` | Detailed weekly savings report with group membership, savings outcomes, balances, activity, payout rotation, and optional visible-section filtering. Tapping a numeric detail can open a breakdown sheet. | `WeeklyReportData`, including daily savings and weekly totals. |
| `STAT_GROUP_PROGRESS` — `MicroBondStatGroupProgressBubble` | Group progress chart with summary measures such as on-time payouts, active members, and average wait. | Labeled time-series chart points and summary labels/values. |
| `STAT_GROUP_SNAPSHOT` — `MicroBondStatGroupSnapshotBubble` | Snapshot of a group's financial allocation as a donut chart, accompanied by member, collection, and next-payout details. | Donut slices and summary values. |
| `GROUP_HEALTH` — `MicroBondGroupHealthBubble` | Radar-chart summary of group/member experience, with strongest and next-focus areas. | Labeled radar values, footer, and optional chart color. |
| `STAT_HISTOGRAM` — `MicroBondStatHistogramBubble` | Histogram of savings or another period-based metric, with total, highest period, average, and footer summaries. | Labeled bars and summary values. |
| `STAT_CUSTOM_CHART` — `StatCustomChartBubble` | User-configured chart bubble with selected title, X/Y labels and values, and colors. | Chart ID, labels, X/Y numeric arrays, primary and secondary colors. |
| `STAT_CANDLESTICK` — `MicroBondStatCandlestickBubble` | OHLC (open, high, low, close) view across periods, with period high/low summaries. | Labeled candles and currency/high/low display values. |
| `STAT_AREA` — `MicroBondStatAreaBubble` | Layered area chart showing contribution components accumulating across periods. | X-axis labels, named/color-coded layers, total summary. |
| `STAT_WATERFALL` — `MicroBondStatWaterfallBubble` | Cash-flow chart showing positive inflows, negative outflows, and optional running-total bars. | Labeled signed bars, currency, net change. |
| `STAT_SCATTER` — `MicroBondStatScatterBubble` | Scatter chart for comparing two member or group measures, with an optional correlation summary. | X/Y points with optional labels, axis labels, correlation. |
| `STAT_BOXPLOT` — `MicroBondStatBoxPlotBubble` | Box-and-whisker distribution chart for each member or period. | Minimum, first quartile, median, third quartile, maximum per entry. |
| `STAT_HEATMAP` — `MicroBondStatHeatmapBubble` | Grid showing activity or payment values across row and column categories, such as members by month. | Row/column labels, cells, color range, footer. |

## System and call bubbles

| Thread type / component | Description | Main content |
| --- | --- | --- |
| `SYSTEM` — `MicroBondSystemBubble` | Centered conversation/system notice, rather than a normal user-authored message. | System text. |
| `MicroBondDeletedBubble` (reusable component) | Placeholder indicating that a message was deleted. Not currently exposed as a `ThreadBubbleType`. | Fixed deleted-message notice. |
| `MicroBondExpiredBubble` (reusable component) | Placeholder indicating that media has expired. Not currently exposed as a `ThreadBubbleType`. | Fixed expired-media notice. |
| `MicroBondCallLogBubble` (reusable component) | Call-history card showing the call kind, duration, and missed/outgoing visual state. It is not currently a thread event type. | Call kind, duration, missed flag. |

## Bubble data and behavior notes

`ThreadEvent` is the common message envelope. Its optional fields carry the
payload for a particular bubble type; for example, `statArea` is used by
`STAT_AREA`, `documentData` by `DOCUMENT`, and `paymentUpdate` by
`PAYMENT_UPDATE`. When a type's expected payload is absent, several branches
render nothing for that event rather than synthesizing a payload.

The regular text/thread bubble surface also uses common metadata such as
incoming/outgoing direction, sender identity/avatar, timestamp, and delivery
state. The thread supports swipe-to-reply for message bubbles other than
`SYSTEM`. Bubble-specific actions include the deposit receipt action, weekly
report detail taps, and payment action prompts. Some other actions (for example
rule-list navigation and security-poll voting) are still placeholders.

The event enum includes no dedicated types yet for call logs, links, GIFs,
stickers, reactions, grouped-media previews, transfer cards, deleted messages,
or expired-media notices. Those components should be considered available UI
building blocks, not evidence that those message types are currently wired into
the thread data/rendering path.
