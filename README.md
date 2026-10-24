# Microbond

# Microbond Application: Architecture & Feature Specification

## 1. Project Overview
**Microbond** is a native Android financial trust and social platform built using Kotlin and Jetpack Compose, with a backend roadmap targeting Rust and Python (FastAPI). The application blends the familiar, frictionless user experience of standard messaging applications (like WhatsApp) with robust financial escrow, smart contracts, and ledger management.

### Key Technical Pillars:
* **UI/UX:** Native Jetpack Compose, WhatsApp-inspired layouts, custom canvas doodle wallpapers.
* **Escrow Integration:** Corporate trust escrow routing (designed for integration with institutions like Stanbic Bank).
* **Data Migration:** Custom decoy architecture to ingest, decrypt (via wa-crypt-tools and Python), and migrate local SQLite WhatsApp backups into a Drift database.

---

## 2. Core UI Interactions: The Overscroll Quick-Voice Gesture
Microbond replaces traditional top-bar voice chat triggers with a fluid, gesture-based interaction within the `LazyColumn` chat thread, utilizing a `NestedScrollConnection`.

### Interaction Sequence:
1. **Elastic Overscroll:** When a user scrolls past the last message in a chat and continues to drag upward, the standard text input field slides downward and fades out.
2. **Floating Quick-Talk Bar:** A dark bottom overlay slides up, presenting a circular button with a waveform icon and a progress ring. Text reads: *"Keep holding to talk"*.
3. **The "Connect" Morph:** Once the user holds long enough for the progress ring to complete, the circle expands into a wide pill shape. The text updates to *"Connect"* and the instruction changes to *"Release to talk"*.
4. **Execution/Cancellation:** Releasing the hold while in the "Connect" state opens the live audio room. Releasing early collapses the UI and restores the standard text input bar.

---

## 3. Saving Group Architectures & Settings
Microbond utilizes familiar terminology (e.g., "Saving Groups," "Circles") rather than intimidating banking jargon. Group settings are dynamically adapted based on the group's financial purpose and are strictly divided into **Public** (all members), **Private** (individual user only), and **Admin Configurable**.

### Universal Settings (Applies to all groups)
* **Public:** Group Icon/Name, Description, Members List, Shared Media/Ledgers, Total Group Balance.
* **Private:** Mute/Custom Notifications, My Payment Method (Mobile Money linkage), Export Chat & Ledger, Report/Exit.
* **Admin Configurable:** Edit Group Info, Send Messages restriction, Add/Approve New Members, Assign Admins.

### Group Type A: Rotating Payout Circle (Merry-Go-Round / ROSCA)
* **Public:** Contribution Amount, Payment Frequency, Payout Roster/Order, Next Payout Countdown, Contribution Streaks/Strikes.
* **Private:** My Payout Destination, Auto-Pay My Turn, Request Turn Swap, Proxy Contributor.
* **Admin Configurable:** Manage Payout Order, Late Fee Penalties, Require Payment Proof, Grace Period Window, Enforce Payout Holds.

### Group Type B: Target Savings Group (Goal-Oriented)
* **Public:** Target Goal Amount, Target Date, Progress Bar, Savings Pace/Velocity, Milestone Celebrations.
* **Private:** My Total Saved, Custom Mini-Goal, Auto-Roundup (spare change deposits).
* **Admin Configurable:** Lock Funds (strict freeze until goal met), Hide Leaderboard (anonymity), Top-Up Matching, Overage Rules.

### Group Type C: Shared Bill / Utility Group
* **Public:** Connected Utilities (e.g., UMEME, NWSC), Upcoming Bills, Average Spending Graph, Disconnection Warnings.
* **Private:** My Split Share (percentage), Dispute a Charge, Pre-Fund Personal Wallet.
* **Admin Configurable:** Trigger Payments, Auto-Pay Bills, Vendor Whitelist, Dispute Resolution Authority.

### Group Type D: Emergency & Welfare Fund (Munno mu Kabi)
* **Public:** Total Welfare Pool, Active Help Requests, Assistance History, Replenishment Target, Trusted Vendor Links.
* **Private:** Request Assistance, Confidential Claim (hide from main chat), Beneficiary Assignment (Next-of-Kin).
* **Admin Configurable:** Approval Process (Admin vs. Group Vote), Maximum Payout Limit, Direct-to-Vendor Payouts, Hardship Waivers.

### Group Type E: Event & Pledge Committee
* **Public:** Event Date Countdown, Total Budget vs. Raised, The Pledge List, Remaining Shortfall, VIP/Sponsor Badges.
* **Private:** My Pledges, Pledge Installment Plan, Issue a Challenge/Match.
* **Admin Configurable:** Allow Anonymous Contributions, Vendor Payments, Lock Pledges, Pledge Caps, Milestone Unlocks.

### Group Type F: Lending & Investment Club
* **Public:** Share Value, Total Group Capital, Active Loans, Group ROI, Default Rate Tracker.
* **Private:** My Equity/Shares, My Active Loan details, Internal Credit Score, Dividend Reinvestment (DRIP) toggle.
* **Admin Configurable:** Set Interest Rates, Loan Guarantor Rules, Maximum Borrowing Limit, Early Repayment Discounts, Default Asset Seizure.

### Group Type G: Shared Wallet
* **Public:** Wallet balance, approved inflows and outflows, and spending categories.
* **Private:** Personal contribution history and visibility of member balances.
* **Admin Configurable:** Spending approval threshold and authorized co-signers.

### Group Type H: Membership Dues
* **Public:** Dues amount and frequency, collection progress, members up to date,
  arrears, and next due date.
* **Private:** Personal dues payment history and outstanding balance.
* **Admin Configurable:** Grace period, late fee, and installment policy.

---

## 4. Advanced Governance, Security & Automation

### Financial Governance & Multi-Signature Audits
* **Multi-Sig Signer Roster (Public):** Displays which members hold co-signing keys.
* **My Approval Authorization (Private):** Secure PIN/Biometric screen for co-signers to approve pending payouts.
* **Require Multi-Signature Approval (Admin):** Prevents any single admin from moving pooled funds without secondary approval.

### Member Reputation & Trust Scores
* **Trust Badges (Public):** Icons showing reliability (e.g., "100% On-Time").
* **Vouch for a Member (Private):** Risking personal credit rating to guarantee a new member.
* **Minimum Trust Score (Admin):** Automated gatekeeping blocking low-trust users from joining or borrowing.

### Automated Liquidity & Escalation
* **Backup Fund Pool Status (Public):** Visibility into the group's emergency buffer for shortfalls.
* **Auto-Debit Consent (Private):** Permission to pull missing dues automatically on the deadline.
* **Liquidity Buffer Coverage (Admin):** Allows the system to temporarily draw from reserves to pay members on time if someone else is late.

### Data Privacy & Offline Audit Controls
* **Group Charter / Constitution (Public):** Digital, accessible rules of engagement.
* **Download Official Statement (Private):** Bank-grade PDF generation for personal audits or external loan applications.
* **Anonymize Member Names (Admin):** Toggles real names to unique IDs for safe external ledger exporting.


Microbond is an Android app prototype for messaging around savings circles and
other trusted-peer activities. Its interface combines chat-style conversations
with group savings summaries, rules, payment prompts, service screens, updates,
notifications, and profile settings.

> **Project status:** This repository currently provides a local, single-device
> prototype. It does not include sign-in, a server, real-time synchronization,
> or a payment-provider integration. Most conversations, groups, balances,
> reports, and service history shown in the UI are sample data. Creating a group
> currently opens a thread by name; it does not save a group or invite members
> to a live conversation.

## At a glance

- **Platform:** Android, minimum SDK 24, target SDK 37, compile SDK 37.
- **Language and UI:** Kotlin and Jetpack Compose (Material 3).
- **Application ID:** `com.microbond.app`.
- **Module:** `:app`.
- **Build system:** Gradle wrapper, currently configured for Gradle 9.6.0.
- **Current user model:** one local app installation and one implicit user; there
  is no account or authentication flow.

## Getting started

Open the repository in Android Studio, let Gradle sync, and run the `app`
configuration on an emulator or Android device. Install the Android SDK platform
for API 37 when prompted. Use a Gradle-compatible JDK (JDK 17 or newer).

From PowerShell at the project root:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

To run instrumented tests on a connected device or emulator:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

The debug APK is produced under `app\build\outputs\apk\debug\`. Android Studio
may create `local.properties` for the local Android SDK path; it is
machine-specific and should not be treated as project configuration.

## Main app areas

The app opens in `MainActivity`, which applies the theme and loads the saved
chat wallpaper before displaying the Compose app. The bottom navigation has
five areas:

| Area | Purpose |
| --- | --- |
| Charts | Chat/group list and conversation threads. The group-creation flow starts here. |
| Updates | Status and discovery-style content. |
| Services | Utility and service purchase/history UI. |
| Notif | Notifications UI. |
| You | Profile and settings, including chat wallpaper selection. |

The main screen is coordinated in `main/MicrobondApp.kt`. Feature UI is
organized by domain under `feature_chart`, `feature_groups`, `feature_updates`,
`feature_services`, `feature_notifications`, and `feature_you`. Shared Compose
components, domain data classes/enums, theme, dummy data, navigation, and payment
flow helpers live under `core`.

## Creating a group

The current group-creation sequence is:

1. Open **Charts** and tap the group/create floating action button. Both
   floating-action-button actions currently open the same creation flow.
2. On **Add participants**, search by contact name or subtitle and tap people to
   select or deselect them. At least one participant is required to continue.
   Selected people appear in a chip row; the next button appears when the
   selection is non-empty.
3. On **New group**, enter a group name and tap the check button to finish.
   Although the field hint says “optional,” the current implementation only
   creates the setup when the name is not blank. The name is limited to 100
   characters and is trimmed before handoff.
4. Optionally open **Group permissions** or pick/crop an icon. These screens
   currently affect only temporary UI state. The disappearing-messages row
   displays “90 days” but is not wired to a settings screen.
5. After confirming, the app closes the creation flow and opens a thread whose
   entity name is the group name.

The flow is coordinated by `feature_groups/ui/newgroup/NewGroupFlow.kt`.
`AddParticipantsScreen` gathers selections, `NewGroupScreen` collects the name
and optional icon, and `NewGroupSetup` is the object passed back to the app.
The setup object currently contains only the name and selected participants.

### Where participant choices come from

The new-group participant screen does **not** currently load the device's
address book. It combines:

- Non-group entries in the hard-coded `sampleChats` list. Their displayed
  subtitle is the sample chat message.
- Locally saved trust connections, displayed with their phone numbers.

Entries are deduplicated by name. The separate invite-member screen can pick a
device contact, save a name and phone number into the trust network, and prepare
an SMS invite or copy the app link. That does not make the contact an app user
or add them to a remotely managed group.

### Current group-creation limitations

- No group record, participant membership, permissions, or icon is persisted
  when the check button is tapped.
- The chosen icon URI is not part of `NewGroupSetup` and is not saved with the
  group.
- Permission switches reset when the permissions screen is left; their values
  are not connected to group creation.
- The new thread is selected by its name. The current implementation does not
  add the new group to the persistent chat list.
- There is no invitation delivery, member acceptance, group synchronization,
  or multi-user access.

These limitations are important if extending this prototype: a real group
feature needs a durable group model and repository, an identity and membership
model, persistence for settings and media, and a backend or other deliberate
multi-device synchronization strategy.

## Data and persistence

### Sample and in-memory data

`core/dummydata` contains the example content used to populate the interface,
including chat previews, sample savings groups, status content, reports,
metrics, and payment examples. Many fields are intentionally illustrative,
including names, phone numbers, timestamps, UGX amounts, savings progress, and
message states. They are not live account, transaction, or group records.

The shared data classes in `core/data_classes_and_enums` describe UI/domain
payloads such as:

- `ChartPreview` for a chat-list row and its unread/favorite/message state.
- `ThreadEvent` and `ThreadBubbleType` for text, media, payment, poll, rules,
  report, and statistics message bubbles.
- Typed payloads for savings summaries, reports, rules, polls, charts, and
  payment updates.
- Enums for message delivery state, member saving state, and savings-group
  duration.

For an inventory of the message bubble types, their displayed content, and
which components are wired into the thread renderer, see [BUBBLES.md](./BUBBLES.md).

Sample savings groups use a shared group record plus a typed purpose payload.
The common record contains identity, description, currency/balance, member
count and representative roster, local visibility, private user preferences,
admin permissions, and shared-media/ledger counts. Purpose-specific payloads cover all nine user-facing types: Merry-Go-Round,
Goal Savings, Bill Split, Welfare & Emergency, Event Committee, Investment
Club, Public Fundraiser, Shared Wallet, and Membership Dues. The consumer labels
are represented by `SavingsGroupType`; their typed sample payloads currently
live in `core/data_classes_and_enums/dataclasses.kt`, and group examples live in
`core/dummydata/dummydata.kt`. They are static examples for the local UI, not
persisted group entities.

Group settings are selected by each sample group's `SavingsGroupType`. The
settings hub provides nine reusable settings screens, populated for each group
type (81 type-and-screen combinations), covering personal settings, type rules,
governance, trust, liquidity, privacy, transaction messages, reminders/reports,
and milestones/security. These prototype controls are local UI state and are
not saved to a database or synchronized.

Compose screen state is generally held in memory. It is not shared with another
installation and may reset when a screen is recreated or the app restarts.

### Data that is saved locally

| Data | Current storage | Notes |
| --- | --- | --- |
| Trust connections | Android `SharedPreferences` (`trust_connections`) | Stores a contact name and phone number as JSON. Phone digits are used to match/update an existing entry. |
| Selected wallpaper | Android `SharedPreferences` (`wallpaper_prefs`) | Stores a drawable resource ID. |
| New groups and messages | Not persisted | Sample chat/thread content is not a server-backed conversation. |
| Group permissions, icon, and disappearing-message choice | Not persisted | Current creation UI does not hand these values into a saved group. |
| Payments and service history | Not persisted as transactions | Payment prompts and service history are demo UI; no payment is processed. |

There is no database, API client, Firebase integration, or network-backed
repository configured in the app module. Keep real personal or financial data
out of the sample data and do not treat the current app as a production savings
or payment system.

### Graph-ready metrics

The metric catalog in `core/dummydata/sampleMetrics.kt` includes numeric
`rawValue` data, a `unit`, ordered `historicalData` samples, and a
`preferredChartType` hint where sample values are available. The catalog also
provides labeled `chartSegments` for pie charts and a two-dimensional
`heatmapData` matrix for heatmap metrics. `getGraphableMetrics()` returns only
metrics with both a current numeric value and a historical series, so a metric
without chart data is not presented as a fabricated zero line.

The sample series currently contain eight ordered values; they are illustrative
periods, not dated transactions. A chart screen should supply time labels based
on the selected period and format its Y-axis using the metric's unit (for
example, abbreviated UGX amounts, percentages, counts, or durations). The
values are demo data and must be replaced by validated repository data before
being used to make financial decisions.

The internal metric taxonomy covers fixed savings, rotating ROSCA, fundraiser,
shared bills/utilities, lending/investment, emergency/welfare, event/pledge,
shared wallet, and association dues, plus cross-group social and
dispute/support categories. This taxonomy is distinct from the nine
consumer-facing `SavingsGroupType` labels. Sample metrics cover each purpose,
including utility spend and invoice resolution, loans/ROI/defaults, welfare
requests and disbursements, event budgets and pledges, wallet balances and
spending approvals, dues collection and arrears, chat/poll/voice activity,
disputed transactions, and admin interventions. These are catalog additions,
not live integrations or transaction monitoring.

## Permissions and device access

The Android manifest declares permissions for contacts, media, and audio.
Specific screens request or invoke device access when needed (for example, the
contact picker and voice recording). Group participant selection itself uses
the app's sample and locally saved trust-contact lists rather than querying
device contacts.

## Project layout

```text
app/
  src/main/
    java/com/microbond/app/
      core/                 Shared UI, data models, sample data, helpers
      feature_chart/        Chat list and preview UI
      feature_groups/       Group flows and trust connections
      feature_notifications/
      feature_services/
      feature_updates/
      feature_you/
      main/                 App shell and bottom navigation
      screens/threadscreen/ Conversation thread
    res/                    Android resources, icons, themes, strings
    AndroidManifest.xml
  src/test/                 Local unit tests
  src/androidTest/          Device/emulator tests
gradle/libs.versions.toml   Dependency and plugin versions
```
In a financial trust platform like Microbond, the group settings (Vault Info) page must bridge standard communication controls with strict ledger management. Here is the architecture for the Vault settings, categorized by visibility and permission levels.

**Public Settings (Visible to All Vault Members)**
These are the foundational details and shared resources that every participant in the group can view.

* **Vault Profile:** The group avatar, vault name, and description (which often contains the group's charter or rules).
* **Financial Terms:** Read-only visibility of the core smart contract, including the required contribution amount (e.g., UGX 50,000), frequency (weekly/monthly), and the total financial goal.
* **Payout Cycle/Roster:** The schedule showing the order of members receiving the pooled funds (crucial for ROSCAs).
* **Participant List:** The directory of all current members in the vault and their roles (Admin vs. Member).
* **Ledger & Shared Media:** Access to all verified transaction receipts, shared documents, and media within the group.
* **Local Preferences:** Personal toggles for the user, such as Mute Notifications, Custom Notification sounds, and Media Visibility (whether to save downloaded receipts to the phone's gallery).

**Non-Public Settings (Private to the Individual User)**
These settings manage a user's personal financial routing and security regarding the specific vault, completely hidden from other members and admins.

* **Linked Contribution Method:** The specific mobile money number or bank account the user has authorized to make deposits into this vault.
* **Payout Destination:** The private account details where the user will receive their lump sum when it is their turn in the ROSCA cycle.
* **Personal Ledger Export:** An option for the user to download their individual contribution history for their own records.
* **Privacy Actions:** Options to **Leave Vault**, **Report Vault** (for fraud or abuse), or **Clear Chat History** (which deletes local chat messages but retains the immutable financial ledger).

**Admin Controls & Permissions (Configurable by Admins)**
These toggles determine what standard members are allowed to do. Admins can lock these down to "Admins Only" or open them up to "All Participants."

* **Edit Vault Info:** Dictates who can change the vault's name, avatar, description, and core financial terms.
* **Send Messages:** Controls the main chat thread. Admins can restrict the group to "Admins Only" (turning it into an announcement board) or allow open discussion.
* **Verify Contributions:** Determines who can approve and verify a deposit receipt. This can be restricted to Admins (centralized trust) or set to require multi-signature peer verification from any participant.
* **Manage Payout Roster:** Controls who has the authority to change the payout order or skip a user's turn in the ROSCA cycle.
* **Add Participants:** Dictates whether any member can add people from their contacts or if adding members is an admin-only privilege.
* **Approve New Members:** A toggle that, when enabled, requires an Admin to manually approve anyone who tries to join the vault via a shareable invite link or QR code.
You make a great point. "Vault" can sound too institutional or intimidating. Using familiar WhatsApp-style language like **"Saving Group"** or **"Circle"** makes the app feel much more approachable, especially for everyday financial trust.

Since different types of saving groups have very different rules, the settings page must dynamically adapt based on the group's purpose. Here is a greatly expanded, comprehensive breakdown of the settings, separated by the three main types of saving groups you would have in Microbond.

---

### 1. Universal Settings (Appears in EVERY Group)

These are the baseline settings that mimic standard WhatsApp behavior, plus basic financial controls.

**Public (Visible to all members):**

* **Group Icon & Name:** The picture and title of the group.
* **Group Description:** The rules, purpose, or welcome message.
* **Members List:** The directory of everyone in the group and their roles (Admin/Member).
* **Media, Links, and Docs:** A gallery view of all shared images, deposit slips, and documents.
* **Pinned Messages:** Quick access to important group announcements or rule lists.
* **Total Group Balance:** The current amount of money sitting in the group's shared escrow account.

**Non-Public (Private to you):**

* **Mute Notifications:** Options to silence chat pings (8 hours, 1 week, Always) while keeping transaction alerts on.
* **Custom Notifications:** Setting a specific ringtone for this specific group.
* **Media Visibility:** Toggle to stop downloaded receipt images from saving to your phone's main gallery.
* **My Payment Method:** Your secretly linked Mobile Money number or card used for sending money to this group.
* **Export Chat & Ledger:** Download a PDF/CSV of your personal contribution history and chat logs.
* **Report / Exit Group:** Private actions to leave the group or report it for suspicious activity.

**Admin Configurable (Admins decide who can do these):**

* **Edit Group Info:** Who can change the name, icon, and description (All Members vs. Admins Only).
* **Send Messages:** Who can chat in the group (All Members vs. Admins Only).
* **Add Other Participants:** Who can invite new people.
* **Approve New Members:** When turned on, anyone joining via an invite link goes into a "Waiting Room" and must be approved by an Admin.
* **Assign Admins:** Promoting other members to help manage the group.

---

### 2. Group Type A: Rotating Payout Circle (Merry-Go-Round / ROSCA)

This is for groups where everyone contributes a fixed amount, and one person takes the whole lump sum each week or month.

**Public (Visible to all members):**

* **Contribution Amount:** The fixed money required per cycle (e.g., UGX 20,000).
* **Frequency:** How often contributions are due (Daily, Weekly, Monthly).
* **The Payout Roster:** A numbered list showing exactly whose turn it is to receive the money this cycle, and who is next in line.
* **Cycle Status Bar:** A visual tracker showing how many people have paid their share for the current round.

**Non-Public (Private to you):**

* **My Payout Destination:** The specific private phone number or account where you want your lump sum sent when it is your turn.
* **Auto-Pay My Turn:** A toggle allowing the app to automatically deduct your contribution from your mobile money when the cycle starts.

**Admin Configurable (Admins decide who can do these):**

* **Manage Payout Order:** Determines who can shuffle the roster or swap people's turns (usually restricted to Admins).
* **Late Fee Penalties:** A toggle to automatically charge a small extra fee to members who miss the contribution deadline.
* **Require Payment Proof:** A strict mode toggle that forces members to upload a screenshot or reference number to clear their balance.

---

### 3. Group Type B: Target Savings Group (Goal-Oriented)

This is for groups saving together for a specific goal (e.g., buying land, an emergency fund, or a community project) where the money stays locked up until the goal is reached.

**Public (Visible to all members):**

* **The Target Goal:** The total amount of money the group is trying to reach (e.g., UGX 5,000,000).
* **Target Date:** The deadline to reach the goal.
* **Progress Bar:** A large visual showing the percentage completed (e.g., "65% Funded").
* **Top Contributors (Leaderboard):** A list showing who has contributed the most money (if the group allows varying contribution sizes).

**Non-Public (Private to you):**

* **My Total Saved:** A private view of exactly how much of the group's total pie belongs to you.
* **Goal Reminders:** Personal alerts you can set to remind you to deposit money (e.g., "Remind me every Friday at 5 PM").

**Admin Configurable (Admins decide who can do these):**

* **Lock Funds:** A strict security toggle that prevents *anyone* (even admins) from withdrawing the money until the Target Goal or Target Date is hit.
* **Hide Leaderboard:** A toggle to make all contributions anonymous, so people can see the total group progress, but not how much each individual person gave.
* **Allow Early Exits:** A rule toggle that determines if a member can pull their specific money out before the goal is reached.

---

### 4. Group Type C: Shared Bill / Utility Group

This is for roommates, families, or business partners who pool money specifically to pay external services (like electricity, water, or internet).

**Public (Visible to all members):**

* **Connected Utilities:** A list of the actual accounts linked to the group (e.g., UMEME Meter #123456, NWSC Account).
* **Upcoming Bills:** A list of statements that are due soon.
* **Payment History:** A clear ledger of every time the group successfully paid a bill.

**Non-Public (Private to you):**

* **My Split Share:** If the bill isn't split equally, this shows your specific percentage (e.g., you pay 40%, roommates pay 30% each).

**Admin Configurable (Admins decide who can do these):**

* **Trigger Payments:** Who has the authority to press the "Pay Bill Now" button using the group's pooled money.
* **Auto-Pay Bills:** A toggle that allows the group to automatically pay the utility company the moment the pooled balance covers the invoice.
* **Add/Remove Utilities:** Who is allowed to link a new meter number or data account to the group.
### 5. Group Type D: Emergency & Welfare Fund

Commonly used for community support (like *Munno mu Kabi*), where members contribute small, regular amounts to build a safety net. The money is only touched when a member faces an unexpected emergency, like a medical bill or a funeral.

**Public (Visible to all members):**

* **Total Welfare Pool:** The current amount of money available to help members.
* **Active Help Requests:** A board showing if a member is currently asking the group for financial assistance.
* **Assistance History:** A ledger showing who received funds in the past and for what reason (e.g., "UGX 200,000 to Sarah for hospital bill").
* **Minimum Standing:** A list showing which members are "Active" (up to date on their monthly welfare fees) and which are "Suspended" (not eligible to ask for help).

**Non-Public (Private to you):**

* **Request Assistance:** A private button to submit a claim for emergency money, where you can secretly upload proof (like a medical bill) for the admins to review.
* **My Total Given:** A summary of how much you have contributed to the welfare fund over the lifetime of the group.

**Admin Configurable (Admins decide who can do these):**

* **Approval Process:** Determines how a member gets emergency money. Can be set to "Admin Approval Only" or "Group Vote" (where >50% of the group must tap 'Approve' on the request bubble).
* **Maximum Payout Limit:** A hard cap on how much money can be given out for a single emergency (e.g., Max UGX 500,000 per claim).
* **Pause Contributions:** A toggle to stop asking for monthly money once the group's safety net reaches a comfortable size.

---

### 6. Group Type E: Event & Pledge Committee

Used for short-term, high-energy goals like funding a wedding, graduation, or a large party. Members make promises (pledges) and fulfill them over time.

**Public (Visible to all members):**

* **The Event Date:** A countdown timer at the top of the chat to the day of the event.
* **Total Budget vs. Raised:** A progress bar showing how much physical cash has actually been collected.
* **The Pledge List:** A clear list showing what everyone promised to bring (e.g., "John: UGX 100k", "Mary: Drinks").
* **Cleared / Uncleared Status:** A checkmark system showing who has actually paid their pledge and who is still pending.

**Non-Public (Private to you):**

* **My Pledges:** Your specific checklist of what you promised to contribute.
* **Pay My Pledge:** A quick button to send mobile money directly matching your exact promised amount.
* **Pledge Reminders:** Automated nudges you can set so you don't forget to pay before the event date.

**Admin Configurable (Admins decide who can do these):**

* **Allow Anonymous Contributions:** A toggle that lets members send money without their name appearing on the public list (shows up as "Guest" or "Well-Wisher").
* **Vendor Payments:** A feature allowing admins to send pooled money directly from the group to external service providers (like a caterer or decorator).
* **Lock Pledges:** A toggle that stops people from adding new promises or editing their existing pledges as the event date gets close.

---

### 7. Group Type F: Lending & Investment Club

A slightly more complex group where members pool money to create a "bank." Members can borrow from this pool and pay it back with interest, growing the group's total wealth.

**Public (Visible to all members):**

* **Share Value:** The cost to buy one "piece" of the group (e.g., 1 Share = UGX 10,000).
* **Total Group Capital:** The total wealth of the group, including cash on hand and money currently loaned out.
* **Active Loans:** A list showing which members currently have borrowed money and when they are supposed to pay it back.
* **Profit / Dividend Announcements:** Alerts showing how much interest the group has earned and distributed back to the members.

**Non-Public (Private to you):**

* **My Equity / Shares:** Exactly how many shares you own and their current value.
* **My Active Loan:** If you borrowed money, a private countdown showing your next repayment date, principal balance, and interest due.
* **Apply for Loan:** A private flow where you request to borrow money from the group pool.

**Admin Configurable (Admins decide who can do these):**

* **Set Interest Rates:** Admins define the penalty or interest rate charged to members who borrow (e.g., 5% per month).
* **Loan Guarantor Rules:** A strict toggle requiring a member to get "co-signers" (other members to vouch for them) before a loan request goes through.
* **Maximum Borrowing Limit:** A setting that restricts how much one person can borrow based on their shares (e.g., "Members can only borrow up to 3x their total shares").
### 1. Group Type A: Rotating Payout Circle (Merry-Go-Round / ROSCA)

**Public (Visible to all members):**

* **Next Payout Countdown:** A live timer at the top of the chat showing exactly how many days/hours until the next lump sum is distributed.
* **Contribution Streaks:** A flame icon or badge next to a member's name showing how many consecutive cycles they have paid on time without a delay.
* **Strike Counter:** A transparent tally of missed or late payments (e.g., "2 Strikes"), holding members socially accountable.

**Non-Public (Private to you):**

* **Request Turn Swap:** A private button to formally ask another member to trade roster spots (e.g., you need the money earlier due to an unexpected bill).
* **Proxy Contributor:** A setting allowing you to nominate a trusted external contact to pay your share this week if your account is empty.

**Admin Configurable (Admins decide who can do these):**

* **Grace Period Window:** A toggle allowing admins to set a buffer (e.g., "24 hours after deadline") before a late fee is officially applied.
* **Substitute Member Allowance:** A rule that dictates if a member can securely sell their spot in the circle to an outsider if they want to drop out mid-cycle.
* **Enforce Payout Holds:** A security toggle that freezes the payout to the recipient until every single member has submitted their contribution.

---

### 2. Group Type B: Target Savings Group (Goal-Oriented)

**Public (Visible to all members):**

* **Savings Pace / Velocity:** A status indicator showing if the group is "Ahead of Schedule," "On Track," or "Falling Behind" based on the target date.
* **Milestone Celebrations:** Automated chat events (with confetti animations) triggered when the group hits 25%, 50%, and 75% of the goal.
* **Fund Lock Status:** A padlock icon in the header indicating whether the money is currently frozen or available for withdrawal.

**Non-Public (Private to you):**

* **Custom Mini-Goal:** A personal tracker where you pledge to cover a specific chunk of the big goal (e.g., "I will personally handle 10% of this target").
* **Auto-Roundup:** A toggle to link your daily mobile money transactions, rounding up your spare change and silently depositing it into the group.

**Admin Configurable (Admins decide who can do these):**

* **Top-Up Matching:** An option for a group sponsor (like an employer or wealthy family member) to automatically match contributions 1-to-1 up to a certain limit.
* **Overage Rules:** Determines what happens if the group saves *more* than the target goal (e.g., "Return to sender" or "Keep in group wallet").
* **Adjust Target Mid-Way:** A strict permission dictating whether the goal amount can be changed after people have already started contributing.

---

### 3. Group Type C: Shared Bill / Utility Group

**Public (Visible to all members):**

* **Average Spending Graph:** A visual chart showing if the group's electricity or water usage is trending up or down compared to last month.
* **Disconnection Warnings:** Urgent, red-tinted system alerts that pin to the top of the chat when a utility provider signals an impending cutoff.
* **Service Outage Notices:** Automated status updates pulled from the utility company (e.g., "NWSC reported pipe maintenance in your area").

**Non-Public (Private to you):**

* **Dispute a Charge:** A private flag you can raise if you believe a specific bill is unusually high and needs auditing before the group pays it.
* **Pre-Fund Wallet:** An option to dump extra money into your personal allocation for the group, so next month's bill is covered without you needing to do anything.

**Admin Configurable (Admins decide who can do these):**

* **Vendor Whitelist:** A strict list of approved billers, preventing anyone from accidentally paying a random merchant using the shared utility funds.
* **Dispute Resolution Authority:** Determines who makes the final call to pay a bill if a member flags it as suspiciously high.
* **Expense Categorization:** Allows admins to force tags on every payment (e.g., Rent, WiFi, Cleaning) to generate accurate monthly breakdown reports.

---

### 4. Group Type D: Emergency & Welfare Fund

**Public (Visible to all members):**

* **Replenishment Target:** A secondary progress bar that appears only after a payout, showing how much the group needs to collect to get the safety net back to 100%.
* **Trusted Vendor Links:** A directory of pre-approved clinics, funeral homes, or pharmacies that the group officially recognizes for direct payments.
* **Recent Voting Outcomes:** A ledger of past emergency requests showing the final group consensus (e.g., "Approved: 18 Yes, 2 No").

**Non-Public (Private to you):**

* **Confidential Claim:** A toggle when requesting money that hides the request from the main chat, sending it *only* to the admins for review (useful for sensitive medical issues).
* **Beneficiary Assignment:** A legal setting where you secretly link the phone number of your next-of-kin to receive your portion of the welfare fund if you pass away.

**Admin Configurable (Admins decide who can do these):**

* **Direct-to-Vendor Payouts:** A strict compliance rule forcing the emergency money to be sent directly to the hospital/vendor's merchant code, rather than into the member's personal wallet.
* **Hardship Waivers:** Allows admins to silently pause a member's monthly contribution requirement without suspending their access to the welfare fund.
* **Cooling-Off Period:** A setting that dictates how many months a brand-new member must contribute before they are legally allowed to request emergency assistance.

---

### 5. Group Type E: Event & Pledge Committee

**Public (Visible to all members):**

* **Remaining Shortfall:** A prominent red number showing exactly how much cash is still missing to cover the event's bottom line.
* **VIP / Sponsor Badges:** Special gold icons that appear next to the names of members who pledged above a certain high-tier threshold.
* **Expense Breakdown:** A pie chart showing how the collected money has actually been spent so far (e.g., 40% Venue, 30% Catering, 30% Unspent).

**Non-Public (Private to you):**

* **Pledge Installment Plan:** A tool that breaks your massive pledge into smaller, automated weekly deductions leading up to the event.
* **Issue a Challenge / Match:** A setting where you publicly promise to double the contribution of the next 3 people who pay their pledges.

**Admin Configurable (Admins decide who can do these):**

* **Pledge Caps:** A limit preventing one single person from pledging more than a set percentage (e.g., 20%) of the total budget, ensuring the event doesn't rely too heavily on one individual.
* **Milestone Unlocks:** A security feature that locks the admin's ability to pay vendors until specific funding milestones are fully met.
* **Cancellation / Refund Rules:** Pre-programmed instructions on whether the app should automatically refund the money to contributors if the event date passes without the target being met.

---

### 6. Group Type F: Lending & Investment Club

**Public (Visible to all members):**

* **Group ROI (Return on Investment):** A green ticker showing the total percentage of profit the group's pooled money has generated this year.
* **Default Rate Tracker:** A transparency metric showing the percentage of loans currently overdue, helping members assess the health of the club.
* **Asset Portfolio:** If the club buys external assets (like land or bonds), this tab tracks the estimated real-world value of those holdings.

**Non-Public (Private to you):**

* **Internal Credit Score:** Your personal trust rating (e.g., "A+") based strictly on your history of paying back this specific group's loans on time.
* **Dividend Reinvestment (DRIP):** A toggle instructing the app to automatically use your end-of-year profits to buy more shares in the group, rather than cashing you out.

**Admin Configurable (Admins decide who can do these):**

* **Early Repayment Discounts:** A rule that automatically reduces the interest owed if a borrower pays back their loan weeks ahead of schedule.
* **Share Dilution (New Issuance):** Determines if the group is allowed to mint "new" shares to let outside investors buy into the club mid-year.
* **Default Asset Seizure:** A smart-contract rule that automatically liquidates a defaulting member's shares to cover their unpaid loan balance.
### 1. Financial Governance & Multi-Signature Audit Settings

These settings enforce cryptographic accountability and prevent fraud or single-point-of-failure administration across any group type.

**Public (Visible to all members):**

* **Multi-Sig Signer Roster:** Displays which specific members (or admins) hold co-signing keys required to release pooled funds.
* **Active Audit Status:** A real-time badge indicating whether the group's current ledger matches the underlying mobile money escrow balance (e.g., "Ledger Audited 5 mins ago").
* **Threshold Quorum Display:** Shows the exact rule for fund releases (e.g., "Requires 3 out of 5 Admin Signatures").
* **Pending Disbursal Queue:** A public list of money waiting to be paid out, along with a live countdown timer before the transaction is executed.

**Non-Public (Private to you):**

* **My Approval Authorization:** A private screen where designated co-signers input their PIN, biometric authentication, or passkey to approve a pending group payout.
* **Whistleblower Flag:** A private option to report fraudulent activity or balance discrepancies directly to the system compliance monitor without alerting group admins.

**Admin Configurable (Admins decide who can do these):**

* **Require Multi-Signature Approval:** A security toggle that prevents any single admin from transferring group funds without secondary approval from another admin or selected member representative.
* **Time-Lock Disbursals:** Enforces a mandatory delay (e.g., 12 hours) between when a payout is requested and when the money is transferred, giving members time to review and object if needed.
* **Bank/Telco Escrow Lock:** A high-security setting that locks the pooled balance directly inside a regulated trust escrow account rather than an individual admin's personal wallet.

---

### 2. Member Reputation & Trust Score Settings

Settings designed to reward reliable financial behavior and protect the group from high-risk or unreliable participants.

**Public (Visible to all members):**

* **Member Trust Badges:** Icons next to participant names showing their reliability history (e.g., "100% On-Time Contributor", "Founding Member", "Vouched by 4 Members").
* **Vouching Web / Network:** A visual map showing which existing member invited or vouched for new participants in the group.
* **Group Health Rating:** An overall score (e.g., 98/100) reflecting how reliably the entire group pays on time and resolves disputes.

**Non-Public (Private to you):**

* **Vouch for a Member:** A private flow where you risk a small portion of your own credit rating or balance to guarantee a prospective member's reliability.
* **Personal Trust Breakdown:** A private dashboard showing how your contribution speed and activity in this group affect your overall score across the entire platform.

**Admin Configurable (Admins decide who can do these):**

* **Minimum Trust Score Requirement:** A gatekeeping rule that automatically blocks users with a low trust rating from joining the group or participating in payouts.
* **Mandatory Guarantor Rule:** Requires new or unverified members to be vouched for by an existing member before they can receive a loan or early ROSCA payout.
* **Automated Suspension Rules:** Automatically revokes chatting or voting privileges for members who fall behind on payments by a set number of days.

---

### 3. Automated Liquidity & Escalation Triggers

Automated rules that keep the group moving smoothly even when members are busy, delayed, or facing unexpected issues.

**Public (Visible to all members):**

* **Backup Fund Pool Status:** Shows if the group maintains an emergency buffer pool to cover temporary shortfalls from late-paying members.
* **Automated Nudge Schedule:** Publicly lists when the app sends automated payment reminders (e.g., "3 days before, 1 day before, and on due date").
* **Default Handling Protocol:** A clear declaration of what steps the group takes when a contribution is missed (e.g., "Day 1: Warning, Day 3: Late Fee, Day 7: Guarantor Cover").

**Non-Public (Private to you):**

* **Auto-Debit Consent:** Permission controls allowing the group's smart contract to pull missing dues directly from your linked mobile money account on the due date.
* **Emergency Contact Assignment:** A private backup phone number used strictly to reach you if you miss a critical deadline before penalties apply.

**Admin Configurable (Admins decide who can do these):**

* **Liquidity Buffer Coverage:** A toggle allowing the system to temporarily draw from the group’s reserve balance to pay a member on time if another member's contribution is delayed.
* **Custom Late Fee Escalation:** Configures whether late fees are fixed amounts or increase daily, and whether those collected fees go to the group reserve or the affected member.
* **Auto-Remind via SMS/Call:** Enables fallback SMS or automated voice calls for members who do not have active mobile internet when contributions are due.

---

### 4. Data Privacy, Export & Offline Audit Controls

Controls that balance transparency among members with data protection, offline record-keeping, and legal compliance.

**Public (Visible to all members):**

* **Group Charter & Constitution Document:** A signed, digital version of the group's legal or formal rules accessible to all members at any time.
* **Export Verification Stamp:** A digital signature verifying that the group's official ledger export has not been tampered with.

**Non-Public (Private to you):**

* **My Data Retention Rights:** Options to manage how long your personal chat logs and activity history remain stored on your device.
* **Download Official Statement:** A formatted, bank-grade PDF statement of your contribution history, usable for physical bank loan applications or official audits.

**Admin Configurable (Admins decide who can do these):**

* **Anonymize Member Names in Exports:** A toggle that replaces personal names with unique IDs when exporting financial reports to outside parties or accountants.
* **Chat History Cleanup:** Allows admins to clear media attachments older than a chosen timeframe (e.g., 90 days) to save storage for all group members while keeping financial records permanently intact.
* **Read-Only Observer Access:** Grants temporary, view-only access to external auditors, legal advisers, or community organizers without giving them chatting or voting permissions.


ActiveAuditStatusScreen
AdvancedGroupPrivacyScreen
ApprovalProcessScreen
.............................
ApprovalQuorumScreen
ArrearsPolicyScreen
ChatHistoryCleanupScreen
......
ContributionReminderScheduleScreen
CoolingoffPeriodForNewMembersScreen
DailySummaryTimeScreen
DefaultAssetSeizureScreen
.......
DownloadOfficialStatementScreen
EarlyRepaymentDiscountScreen
EmergencyContactScreen
EncryptionScreen
......
EscrowLockScreen
ExitGroupScreen
ExportMyRecordsScreen
FeeExemptionsScreen
.........
GracePeriodScreen
GroupHealthRatingScreen
InterestRatesScreen
InternalCreditScoreScreen
..........
IssueAChallengeMatchScreen
LateFeeScreen
LiquidityBufferCoverageScreen
LoanGuarantorRulesScreen
.......
ManagePayoutOrderScreen
MaximumBorrowingLimitScreen
MaximumPayoutLimitScreen
MilestoneUnlocksScreen
...........
MinimumTrustScoreScreen
MultisignatureSignerRosterScreen
MuteCustomNotificationsScreen
MyActiveLoanDetailsScreen
......
MyApprovalAuthorizationScreen
MyContributionHistoryScreen
MyContributionsScreen
MyDataRetentionScreen
......
MyDonationReceiptsScreen
MyDuesStatusScreen
MyEquitySharesScreen
MyPayoutDestinationScreen
........
MyPledgesScreen
MySplitShareScreen
MyTotalSavedScreen
MyTrustBreakdownScreen
NewShareIssuanceScreen
.......
NotificationsScreen
ProxyContributorScreen
ReportGroupScreen
RequestTurnSwapScreen
SharedMediaLinksAndDocsScreen
.
SpendingApprovalThresholdScreen
SubstituteMemberAllowanceScreen
TimelockDisbursalsScreen
WeeklyReportDayScreen
9:49 PM
