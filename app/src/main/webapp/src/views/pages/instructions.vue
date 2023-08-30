<template>
<div class="about">
  <h1>Instructions</h1>
  <h2>How to use the tool</h2>
  <p>The system will give you more useful results if you have an idea of what kind of information you are looking for before you begin your search. You may find it helpful to make a note of your search terms as you go, so you can explore them using different sections of the tool.</p>

  <p>The intention is to show the relationships which exist between a range of data sources and the National Performance Indicators.</p>

  <p><b>Notes</b>  </p>
  <ol>

      <li>Tags denote search terms and can be used multiple times. (If no references are present then the search term does not exist within the documents in the database.)

      </li>
      <li>Filters allow content to be narrowed down and ‘revealed' in the interface.     </li>
      <li>Graph display - the graph is large and complex; it is beneficial to view it on a large screen, for example 1080p (1920x1080) at 100% zoom.    </li>

  </ol>
  <hr>

  <p>
      An example search is outlined below to provide a guide of the tool and menu items.</p>
      <p>As the system is based on search we assume that there is a term present within the inquiry. In this example we use the term ‘Dog’.</p>

  <p>This enquiry may be prompted for a range of reasons. These may include FOI, media , Ministerial interest and for operational pusposes. </p>

  <h2>Example Search</h2>
  <p>If you enter the search term ‘dog’ in the ‘Data references’ section, you retrieve three documents - ‘Dog Control’, ‘Review of danger’ and ‘Fireworks review’. 
  If you want to explore ‘Fireworks review’, you can click on ‘Document details’ and use the tree diagram to discover more information. </p>

  <img src="/image5.png" alt="Example GUI representation" width="1200px" height="675px">

  <p>You can see that this document is from the ‘Education, Communities and Justice’ Directorate, relates to the primary outcome ‘Communities’ and is from the time period ‘2020-2021’. </p>
  <p>To explore this further, use the graph view (‘Graph Overview’) and enter the term ‘dog’ in the search bar as a ‘tag’. </p>

  <img src="/image4.png" alt="Example GUI representation" width="1200px" height="675px">

  <p></p>This reveals four related Directorates. 

  <p></p>Then you can use the filters located below the search bar.
  <p></p>More (in blue) reveals additional information which is related to the connections and information which is present on the system. We then add Primary Outcomes (green).

  <img src="/image2.png" alt="Example GUI representation" width="1200px" height="675px">

  <p> This shows that there are relationships with Climate Change and Communities. That can be hovered on and will reveal further information. </p>

  <p> Next we can apply Keywords which reveals additional information on the links with predefined keywords. </p>
  <img src="/image3.png" alt="Example GUI representation" width="1200px" height="675px">
  <p> This shows terms such as Dangerous Dogs Act and Fireworks Action Plan. </p>
  <p> From there we could explore this in the Graph Circle View and apply the tags Dogs and Fireworks. </p>
  <img src="/image1.png" alt="Example GUI representation" width="1200px" height="675px">
  <p> Here we can detect the Lead Official. </p>
  <p> We can then return and look at another range of connections and points of intersection by a search based on the name of the Lead Official. </p>

  <img src="/image6.png" alt="Example GUI representation" width="1200px" height="675px">
  <hr>
  <h2>Menu Overview</h2>
  <h3>Data References</h3>
  <p>The ‘Data references’ page provides an inventory of all the documents held within the database, as well as basic ‘counts’ across a range of references.
  The information represented is based on what was available and its quality. If no data has been provided under a particular category it will appear blank.</p>

  <p>Enter a search term to find and explore any documents relevant to your chosen terms. Clicking on ‘document details’ provides a tree diagram which shows the links the document has to pre-agreed categories, for example the Directorate, policy title or strategic priority.</p>

  <p>On the right hand side in the ‘Actions’ column, there are two prompts which will link to a subsequent view. These present more details about the selected document.</p>
  <h4> Details</h4>
  <p>This presents the details of the document in a Tree Structure and reports a range of references. </p>
  <p>The header indicates the document title. The tree displays information such as the time period, strategic priorities and policy title.</p>

  <h4>Outcomes Relationships Graph</h4>
  <p>This shows the relationships between the Directorate and other documents which are present in the system from the Directorate’s Business plan. It also shows the contextual relationships which exist across the system.</p>

  <p>In the search bar additional terms can be added to narrow the references down and therefore narrow down the inquiry. You might use the original search term, or you may wish to further explore the relationships which exist and why.

  At the top of the page there are several filters. These can be toggled to show what references are present.

  More (markers from the fields filled within the document sources eg Lead Official, Policy Title or Related Year)
  Primary outcomes (Performance indicators)
  Secondary outcomes (Performance indicators)
  Keywords extracted from ‘free text’ within the documents.</p>

  <h3>Graph Overview</h3>
  <p>This provides an overview and exposes contextual links between structure, activities and outcomes in the data provided. The purpose is to allow users to observe the relationships which exist within and between a range of documents based on several terms of reference which are contained within the Graph Database. These are derived from both structural data (e.g. contained within a single cell in a spreadsheet), as well as from analysing texts contained within written descriptions.</p>
  <p>At first sight the graph appears vast, expansive and complex, so it’s important to narrow down the search using relevant search terms and filters. You can add one or more key terms to the search bar, and then narrow the view further using the four filters below (Primary outcomes, Secondary outcomes, Keywords and Other). These follow a colour key to make it easier to explore the results.</p>

  <p>
  This view allows a user to explore and discover many different associations across the available information.
  </p>

  <p>
  By using the search bar topics, themes and references can be narrowed to allow the discovery of related activities. This allows many enquiries to be supported.</p>
  <h3>Graph Circle View</h3>
  <p>This works as above but offers an alternative way of displaying the results. It is best used once greater insight on the key terms and references has already been gained using the main Graph Overview. It visualises the information with a narrower frame which can assist observing the relationships which are present.</p>
  <hr>

  <h2>Example - Sankey</h2>
  <p>This presents an example as to how a range of views can be programmed using the end-points associated with a Graph Database. It is derived from Open Source charting tools contained within libraries such as <a href="https://echarts.apache.org/en/index.html" target="_blank"> Apache Echarts</a></p>

  <ul>
  <li>Reading from left to right, we can observe the connections between an operational unit and how it supports specific outcomes through a range of activities.</li>
  <li>It can also be read right to left to observe the relationship between activities, outcomes, and how these are related within the organisational structure.</li>
  <li>There are  keywords present. These are associated with the activity. This enhances the view by identifying the associated descriptive narrative.</li></ul>
  <p><b>Note</b> this is only presented as an example and is not searchable. It is constrained by using a limited number of references.</p>
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
