This document contains several notes about things in code that are not clear to me or I think could be done better.


BIGGER:

- mixed code of core components with json serializing library - any change to json library will cause whole code to be modified...
- lack of abstraction on collections (Map<String, Double> == CostVector? etc.)
- Datum - confusing name, no docs on this topic - this is core element!
- Datum - why it handles Gold and nod Gold data? we could change hierarchy and make it subling with GoldDatum and stop if-ing...


SMALLER:

- We have class Category - which is not used everywhere (sometimes it is replaced with string) - we should be more consequent
- is there any difference between **label** and **category**? if not why do we use two different names for one thing?
- We have in Datum assigned labels - why don't make another class that stores datum and label that are assaigned to it - Datum is object that make sense without labels
- confusion matrix - we have interface(?) - and we don't use it in DawidSkene - that way we do some


IN 
GAL:

- class Datum does way to much - calculations of probabiliteis with different method should be in separate class

