
const toggleSidebar = () => {
   
    if($(".sidebar").is(":visible")) {
        //true
        // band karna hai 
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");

    } else{
      //false
      // dikhana hai 
      $(".sidebar").css("display","block");
      $(".content").css("margin-left","20%");
    }

};

const search = () => {
  let query = $("#search-input").val();
  console.log(query);

  if (query === '') {
    $(".search-result").hide();
  } else {
    console.log(query);

    // Sending request to the server
    let url = `http://localhost:8080/search/${query}`;
    fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        console.log(data);
        let text = '<div class="list-group">';
        data.forEach((contact) => {
          text += `<a href="#" class="list-group-item list-group-action">${contact.name}</a>`;
        });

        text += '</div>';
        $(".search-result").html(text);
        $(".search-result").show();
      })
      .catch((error) => {
        console.error('Error fetching data:', error);
      });
  }
};


