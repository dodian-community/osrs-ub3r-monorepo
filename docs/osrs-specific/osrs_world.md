# OSRS Worlds
This documentation won't be very long, but will explain some things that can be useful to know about how the OSRS client loads worlds.

**World Properties:**
- **id** - (number)
- **host** - (string) The hostname of the world for the client to use to connect
- **activity** - (string) The mouseover/tooltip text when mousing over a world in the world list
- **properties** - (number) The sum of all the applied world flag numbers combined
- **location** - (number) The 'id' of the location flag to use in the world list
- **population** - (number) The amount of players online on this world

### World Properties / Flags
Each world has a set of flags, which is packed into one number called `properties`. Each flag has an 'id' or number representing it. The `properties` value is the sum of all the applied flag ids for this world.

**List of flags:**
- Members Only (1)
- *Unknown (2)*
- PVP (4)
- *Unknown (8)*
- Beta (33_554_432)
- Deadman (536_870_912)
- *Unknown (1_073_741_824)*

### World Location
Each world has a location, which defines which flag to use for the entry in the client's world list. This is just a number 'id' for each location. Presumably it's where Jagex is hosting their worlds.
- United States (0)
- United Kingdom (1)
- Australia (3)
- Germany (7)
