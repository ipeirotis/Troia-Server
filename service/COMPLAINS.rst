This document contains several notes about things in code that are not clear to me or I think could be done better.


BIGGER:

- mixed code of core components with json serializing library - any change to json library will cause whole code to be modified...
- lack of abstraction on collections (Map<String, Double> == CostVector? etc.)
==> PANOS: Good idea. We may need to look at a list of these. Also, Guava may offer better support for collections that we need (e.g. bidirectional maps)
- Datum - confusing name, no docs on this topic - this is core element!
==> PANOS: Datum is an object/piece of data. However, "object" is a reserved keyword in Java. We could name it data, but this is plural. So, Datum is effectively something that represents the object that is being labeled.
- Datum - why it handles Gold and nod Gold data? we could change hierarchy and make it subling with GoldDatum and stop if-ing...
==> PANOS: Because the item may be marked as gold during execution.

SMALLER:

- We have class Category - which is not used everywhere (sometimes it is replaced with string) - we should be more consequent
==> PANOS: Not a bad idea. We use string mainly as a lightweight representation of category, and mainly when we do not care about setting a prior value for the category etc.
- is there any difference between **label** and **category**? if not why do we use two different names for one thing?
==> PANOS: Yes, there is a difference. A label is a value assigned by a user. A category has a prior attached to it. It is almost the difference between "dollar" (the currency), and a "dollar bill" (the bank note). The former represents the general notion of a currency, the latter is just an instance where the currency is being used.
- We have in Datum assigned labels - why don't make another class that stores datum and label that are assaigned to it - Datum is object that make sense without labels
==> PANOS: That was just a matter of practicality (in the same way that we have a list of AssignedLabels in each Worker object). If we build Troia on top of a KV store and forget about referential integrity, we can easily represent assignedLabels as a bidirectional map (or relation <Datum, Worker, Label> indexed on both Datum and Worker)
- confusion matrix - we have interface(?) - and we don't use it in DawidSkene - that way we do some


IN 
GAL:

- class Datum does way to much - calculations of probabiliteis with different method should be in separate class

