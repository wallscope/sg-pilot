<template>
<div class="about">
  <h1>Instructions</h1>
  <h2>How to use the tool</h2>
  <p>The system will give you more useful results if you have an idea of what kind of information you are looking for before you begin your search. </p>

  <p>The intention is to show the relationships which exist between a range of data sources. The data contains references associated with Business Planning and the Programme for Government.</p>

  <p><b>Notes</b></p>
  <ol>
      <li>Multiple search terms can be used in one search (If no references are present then the search term does not exist within the documents in the database.)
      </li>
      <li>Filters allow content to be narrowed down and ‘revealed' in the interface.     </li>
      <li>Graph display - the graph is large and complex; it is beneficial to view it on a large screen, for example 1080p (1920x1080) at 100% zoom.    </li>
  </ol>
  <hr>

  <p>An example search is outlined below to provide a guide of the tool and menu items.</p>
  <p>A search may be prompted for a range of reasons. These could include FOI and media requests, Ministerial interest and for operational purposes. </p>

  <h2>Example Discovery</h2>
  <p>If you enter the search term <b>‘autism’</b> in the ‘Data references’ section, you retrieve five documents. You can click on ‘Document details’ and use a tree diagram to discover more information.</p>

  <img src="/image6.png" alt="Example GUI representation" width="1200px" height="675px">

  <p>You can see the documents from the <b>‘Directorate for Mental Health’</b>. </p>
  <img src="/image7.png" alt="Example GUI representation" width="1200px" height="675px">
  <p>This relates to the Primary outcome <b>‘Human Rights’</b> and the Secondary Outcome <b>‘Health’</b>, and is from the time period ‘2023-2024’.</p>
  <img src="/image1.png" alt="Example GUI representation" width="1200px" height="675px">
  <p> To explore the topic <b>‘autism’</b> further, use the graph view (‘Graph Overview’).

Entering ‘D’ in the Directorate search bar brings up all of the Directorates. This returns a large number of results which need to be refined.

This can be done by adding the search term ‘autism’ in the search bar below. </p>
  <img src="/image2.png" alt="Example GUI representation" width="1200px" height="675px">
  <p>The resulting graph reveals a number of related policies and a related <b>Business Commitment</b>.</p>
  <img src="/image4.png" alt="Example GUI representation" width="1200px" height="675px">
  <p>Selecting the node for <b>‘Directorate for Mental Health’</b> reveals the details of the relationships displayed in a table below the graph. This includes the lead official for each policy area and the location of related documents.</p>
  <img src="/image5.png" alt="Example GUI representation" width="1200px" height="675px">
  <p>The list of filters is displayed on the left hand side of the page. Selecting <b>‘Keywords’</b> reveals further relationships which can be explored in the graph. Other outcomes can be selected, for example <b>‘Child poverty’</b>,<b>‘Education’</b>,<b> ‘Health’</b> or <b>‘Human Rights’</b>, to explore multiple relationships between the entities.</p>
  <img src="/image3.png" alt="Example GUI representation" width="1200px" height="675px">
  <h2>Menu Overview</h2>
  <h3>Data References</h3>
  <p>The ‘Data references’ page provides an inventory of all the documents held within the database. The information represented is based on what was available and its quality. If no data has been provided under a particular category it will appear blank.

Enter a search term to find and explore any documents relevant to your chosen terms.

On the right hand side in the ‘Actions’ column, there are two prompts which will link to a subsequent view. These present more details about the selected document.</p>

  <h4>Details</h4>
  <p>This presents the details of the document in a Tree Structure and reports a range of references.

The header indicates the document title. The tree displays information such as the time period, strategic priorities and policy title.</p>

  <h3>Graph Overview</h3>
  <p>This provides an overview and exposes contextual links between structure, activities and outcomes in the data provided. The purpose is to allow users to observe the relationships which exist within and between a range of documents based on several terms of reference which are contained within the Graph Database. These are derived from both structural data (e.g. contained within a single cell in a spreadsheet), as well as from analysing texts contained within written descriptions.
</p>
<p>Search terms and filters will help you to refine your search. You can add one or more key terms to the search bar, and then narrow the view further using the drop down list of filters. These follow a colour key to make it easier to explore the results.

This view allows a user to explore and discover many different associations across the available information.</p>
<h3>Graph Circle View</h3>

  <p>
    This has the same functionality as the Graph Overview but offers an alternative circular way of displaying the results. It is best used once greater insight on the key terms and references has already been gained using the main Graph Overview. It visualises the information with a narrower frame which can assist observing the relationships which are present.
  </p>
  
  <hr>

  <h2>Example - Sankey</h2>
  <p>A Sankey diagram has been provided as an example of the kind of visualisation that can be produced using the data. It is a more ‘static’ visualisation and as such is not as well suited as the Graph Overview for exploring the references. It is included to demonstrate other types of visualisations possible for future development.</p>

  <p>Reading from left to right, we can observe the connections between an operational unit and how it supports specific outcomes through a range of activities. It can also be read right to left to observe the relationship between activities, outcomes, and how these are related within the organisational structure.</p>

</div>
</template>
<style lang="scss" scoped>
.about {
  padding: 20px;
  background-color: rgb(248, 250, 252);
  border-radius: 10px;
  border: 2px solid rgb(132, 168, 204);
}
ol li {margin-left: 50px;}
p {margin-top: 15px;}
hr {margin-top: 15px;}
</style>
