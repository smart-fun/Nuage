# Nuage ☁️

**Nuage** is a lightweight, local-first Android database library that automatically synchronizes your data with the user's personal cloud storage (Google Drive, Dropbox, etc.).

It provides a high-level abstraction over SQLite, masking the complexity of SQL queries and synchronization logic while ensuring data persistence and cross-device consistency using simple JSON files.

## Key Features

- **SQLite Backend**: Fast local access with the reliability of a structured database.
- **Cloud Sync**: Synchronizes data as batched JSON files on the user's own cloud storage.
- **Privacy First**: No central server. Data belongs to the user and stays in their personal cloud.
- **Developer Friendly**: No SQL boilerplate. Use a simple, fluent API to manage tables and records.
- **Conflict Resolution**: Built-in "Last Write Wins" strategy based on server-side timestamps.

## Project Structure (WIP)

The library is designed with a modular architecture:

- `Nuage.Core`: The main engine handling SQLite abstraction and the transaction system.
- `Nuage.Drive`: (Coming Soon) Google Drive synchronization module.
- `Nuage.Dropbox`: (Coming Soon) Dropbox synchronization module.

## Getting Started (Preview)

Nuage uses a transaction-based system to ensure data integrity.




```java
// 1. Initialize the database
NuageDatabase db = new NuageDatabase(context, "my_database");
NuageTable clients = db.getTable("clients");

// 2. Define your schema (One-time or dynamic)
clients.addColumn("firstName", NuageColumn.Type.STRING);
clients.addColumn("isPremium", NuageColumn.Type.BOOLEAN);

// 3. Add data
NuageRecord record = new NuageRecord()
 .put("firstName", "Jean")
 .put("isPremium", true);

clients.add(record);

// 4. Apply changes (Executes SQLite transactions and prepares Cloud sync)
clients.apply((success, exception) -> {
 if (success) {
 // Data is saved locally and queued for synchronization
 }
});
```

## How it Works

Nuage manages a hidden SQLite database locally. When you `apply()` changes:

1. It updates the local database within a secure transaction.
2. It updates a `schema.json` file representing the table structure.
3. It partitions your records into small `items_n.json` batches to optimize network transfers and memory usage.

## License

Copyright 2026 Arnaud Guyon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
