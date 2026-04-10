
## nuage.database

Main entry for creating/initializing a database and manipulate NuageTables NuageRecords.
 
- add an "_id" AUTOINCREMENT column so that each record has a unique numeric ID starting from 1 (will be used to group records in batch).
- when a database is updated, save a "changelog" to a centralized database table (record _uuid, _id).
This table will be used by Core.sync to synchronize: read the 1st record, compute the batch number (ie _id/100 for ) and then request the changes for all ids of the batch.
- add a "_deleted" column to track record removals without physical deletion.

## nuage.json

Serialization / deseralization of Record in json

## nuage.sync

Sync engine. provides core code for sync, but final read/write is done in a separate provider (DropboxProvider, GoogleDriveProvider, etc)
several strategies could be implemented: instant sync, on demand sync, periodic sync, inactivity sync.
