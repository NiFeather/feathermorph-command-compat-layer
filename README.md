## feathermorph-command-compat-layer
This is a compatibility layer for translating V3 commands into V2/V1 format

## Why?
Starting from V3, the protocol uses JSON to carry messages between the server and the client, and command arguments are now recorded using a Map.

These designs make V3 incompatible with V2/V1, so we need a translation layer to translate V3 commands into the V2/V1 format.

So this is here.

## Usage
1. Import the library
2. Create an instance of `LegacyCommandConverter` and `LegacyCommandProcessor`

### From V3 to V2/V1
1. To convert a V3 command to V2/V1, use `LegacyCommandConverter#toNetheriteCommand(AbstractS2CCommand)` to get a matching V2 instance
   - Throws `RuntimeException` if the given instance doesn't have a matching legacy command instance

### From V2/V1 to V3
1. Create an V2 command instance from `LegacyCommandProcessor#processLegacyCommandLine(TPlayer, String)`
2. Use `LegacyCommandConverter#fromNetheriteCommand(NetheriteC2SCommand)` to get a matching V3 instance
   - Throws `RuntimeException` if the given instance doesn't have a matching legacy command instance