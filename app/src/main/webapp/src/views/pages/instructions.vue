<template>
<div class="about">
  <h1>Knowledge Graph Overview</h1>
  <p>This is a graphical user interface to support the exploration and discovery of a range of relationships within the Scottish Government. Its intention is to improve collaboration at a human and information level.
The following views allow for several user scenarios.</p>
  <h3>Large Graph</h3>
  <p>Organisational overview and linkages between structure, activities and outcomes.
This represents the contextual links extracted from the data provided. The purpose is to allow users to observe the relationships which exist within and between a range of documents based on several terms of reference which are contained within the Graph Database.
These are derived from both structural data e.g. contained within a single cell in a spreadsheet, as well as references derived from analysing texts contained within written descriptions.</p>
<p>At first sight it appears vast, expansive and complex. Yet the relationships are mapped and this can be viewed as a representation of organisational activity alongside interdependencies and related key-words.
There are X filters which can be used to narrow the view. These allow for X, Y, Z etc. These follow a colour key.</p>
<h3>Sankey Chart</h3>
<p>This represents the throughput of resources towards related outcomes.
Reading from Left to Right we can observe the connections between an operational unit and how it supports specific outcomes through a range of activities.</p>
<p>It can also be read Right to Left to observe the relationship between activities, outcomes, and how these are related within the organisational structure.
The thickness of the line represents a 'count' per activity.</p>
<h3>Tree Chart</h3>
<p>This represents organisational relationships and people.
This is driven by individual documents. A document represents the information we have about the organisation and the activities which are project based.
Within this view you can observe the structure and its key components as they relate to the activity and how that relates to key personnel.</p>
<p>It also contains key words associated with the activity. This enhances the view by identifying the associated descriptive narrative.</p>
<h3>Overview</h3>
<p>This provides an inventory view of the system based on documents.
That and basic ‘counts’ across a range of references.</p>
<p>NOTE - the above is not exhaustive as the information represented is based on what was available and its quality.</p>
</div>
</template>
<style lang="scss" scoped>
.about {
  padding: 20px;
  background-color: rgb(248, 250, 252);
  border-radius: 10px;
  border: 2px solid rgb(132, 168, 204);
}
</style>
