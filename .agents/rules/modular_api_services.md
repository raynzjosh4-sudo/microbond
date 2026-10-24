---
name: modular-network-api-services
description: Never bloat primary API service files; create dedicated subpackages and sub-interfaces under network/api/ for new endpoints and DTOs.
---

# Modular Network API Services & Subfolder Organization

### 1. No Bloating of Primary Service Files
- Never append new endpoint definitions, requests, or DTO response models to monolithic service files (e.g., `GroupsApiService.kt`, `MicrobondApiService.kt`).
- If a primary API service file exceeds **300 lines**, new endpoints must be extracted into a dedicated domain service.

### 2. Dedicated Subfolder Structure
- Group related APIs and their data classes into dedicated subpackages under `com.microbond.app.core.network.api.goupapis/` or `com.microbond.app.core.network.api/`:
  - `groupallsettingsapiservice/`: All group settings APIs (Governance, Timelock/Financial, Goal Controls, Member Visibility, Privacy).
  - `chatapiservice/`: Chat, messaging, attachments, read receipts.
  - `rotatinggroupapiservice/`: ROSCA/Merry-go-round rotation schedules, swap turns.
  - `membershipapiservice/`: Personal group membership, dues, statement downloads, personal savings growth.

### 3. Unified Interface Aggregation
- Have the sub-interface declare its specific endpoints and DTOs cleanly:
  ```kotlin
  package com.microbond.app.core.network.api.goupapis.groupallsettingsapiservice

  interface GroupSettingsApiService { ... }
  ```
- Have `com.microbond.app.core.network.api.MicrobondApiService` extend the new interface:
  ```kotlin
  interface MicrobondApiService :
      AuthApiService,
      UserApiService,
      ContactsApiService,
      GroupsApiService,
      RotatingApiService,
      GroupChatApiService,
      AnalyticsApiService,
      DepositWidgetApiService,
      GroupSettingsApiService { ... }
  ```
- This maintains 100% backward-compatibility across all callers (`NetworkClient.apiService.<call>`) without bloating any single file.
