var allArticles = get("/articles");
var shoppingcart;
var userArray = get("/users");
var options = "<option>1</option>";
for(let j = 2 ; j < 11; j++){
    options = options + "<option>"+j+"</option>";
}
var currentUser;
var currentShoppingcart;
var bankaccounts;
var allOrders;

//Gets the Elements for the given path
function get(path){
    let articlesRes;
    $.ajax({
        type: "GET",
        url: path,
        contentType: "application/json",
        async: false,
        dataType: "json",
        success: function(res) {
            articlesRes = res;
        }
    })
    return articlesRes;
};

var foundArticlesRes;
$('#search').click(function(){
    let eingabe = $("#article").val();
    if(!eingabe){
        $("#articletable").empty();
        renderArticles(allArticles);
    }else{
        $.ajax({
            type: "GET",
            url: "users/"+currentUser.id+"/articles/"+eingabe,
            contentType: "application/json",
            async: false,
            dataType: "json",
            success: function(res) {
                foundArticlesRes = res;
                $("#articletable").empty();
                renderArticles(foundArticlesRes);
            }
        })
    }
});



//Pre-fills all input-fields with the current profile-data
function currentProfile(){  
    $('#updateUsername').attr("value",currentUser.username);
    $('#updatePassword').attr("value",currentUser.password);
    $('#updateHonorifics').attr("value",currentUser.honorifics);
    $('#updateFirstname').attr("value",currentUser.firstname);
    $('#updateLastname').attr("value",currentUser.name);
    $('#updateStreet').attr("value",currentUser.address.street);
    $('#updateHousenumber').attr("value",currentUser.address.housenumber);    
    $('#updatePostcode').attr("value",currentUser.address.postcode);
    $('#updateCity').attr("value",currentUser.address.city);    
}

//Updates profile with a PUT, using the text-field-values
$('#updatebtn').click(function() {
    let userData;
    $.ajax({
        type: "PUT",
        url: "users/"+currentUser.id,
        contentType: "application/json",
        async: false,
        dataType: "json",
        data: JSON.stringify({
            "username": $('#updateUsername').val(),
            "password": $('#updatePassword').val(),
            "honorifics": $('#updateHonorifics').val(),
            "name": $('#updateLastname').val(),
            "firstname": $('#updateFirstname').val(),
            "address": {
                "postcode": $('#updatePostcode').val(),
                "city": $('#updateCity').val(),
                "street":$('#updateStreet').val(),
                "housenumber": $('#updateHousenumber').val(),
            }
        }),
        success: function(res) {
            userData  = res;
            alert('Änderungen wurden übernommen!');
        },
        error: function(xhr) {
            let errMsg = JSON.parse(xhr.responseText);
            alert(errMsg[0].message);
        }
    });
    currentUser = get("/users/"+currentUser.id);
    return userData;
});

//Finds the order without timestamp
function getShoppingcart(){
    var allOrders = get("users/"+currentUser.id+"/orders");
    for(var i = 0; i < allOrders.length; i++){
        if(allOrders[i].isOrder == false){
            return allOrders[i];
        }
    }
};

//adds a article amount-times into the shoppingcart
function addToShoppingcart(article, amount){
    $.ajax({
        type: "POST",
        url: "/users/"+currentUser.id+"/orders/"+currentShoppingcart.id+"/add/" + article.id,
        contentType: "application/json",
        data: JSON.stringify({
            "articleAmount": amount
        }),
        async: false,
        dataType: "json",
        success: function(res) {
            articlesRes = res;
            alert(article.name+" wurde "+amount+" mal in den Warenkorb gelegt!");
        },
        error: function(xhr) {
            let errMsg = JSON.parse(xhr.responseText);
            alert(errMsg[0].message);
        }
    })
    shoppingcart = get("/users/"+currentUser.id+"/shoppingcart");
    currentShoppingcart = getShoppingcart();
    renderShoppingcart();
}

function deleteBankAccount(position){
    bankaccounts = get("/users/"+currentUser.id+"/bankaccounts");
    $.ajax({
        type: "DELETE",
        url: "users/"+currentUser.id+"/bankaccounts/"+position,
        contentType: "application/json",
        data: JSON.stringify({
        }),
        async: false,
        dataType: "json",
        success: function(res) {
            articlesRes = res;
        },
        error: function(xhr) {
            let errMsg = JSON.parse(xhr.responseText);
            alert(errMsg[0].message);
        }
    })
    $('#bankaccountstable').empty();    
    renderBankaccounts();
}

function deleteFromShoppingcart(article, amount){
    shoppingcart = get("/users/"+currentUser.id+"/shoppingcart");    
    $.ajax({
        type: "POST",
        url: "users/"+currentUser.id+"/orders/"+currentShoppingcart.id+"/remove/"+ article.id,
        contentType: "application/json",
        data: JSON.stringify({
            "articleAmount": amount
        }),
        async: false,
        dataType: "json",
        success: function(res) {
            articlesRes = res;
        },
        error: function(xhr) {
            let errMsg = JSON.parse(xhr.responseText);
            alert(errMsg[0].message);
        }
    })
    if(shoppingcart.length == 0){
        $('#shoppingcarttable').append('<p id="emptyShoppingcart">Der Einkaufswagen ist leer!</p>');
        return;
    }
    $('#shoppingcarttable').empty();   
    renderShoppingcart();
}

function deleteOrder(order){
    allOrders = get("users/"+currentUser.id+"/orders");    
    $.ajax({
        type: "DELETE",
        url: "users/"+currentUser.id+"/orders/"+order,
        contentType: "application/json",
        data: JSON.stringify({
        }),
        async: false,
        dataType: "json",
        success: function(res) {
            articlesRes = res;
            alert('Die Bestellung wurde storniert!');
        },
        error: function(xhr) {
            let errMsg = JSON.parse(xhr.responseText);
            alert(errMsg[0].message);
        }
    })
    $('#allOrderstable').empty();    
    renderAllOrders();
}
   
$('#login').click(function() {
    var userArray = get("/users");                
    let userEingabe = $("#username").val();
    let passwordEingabe = $("#password").val();
    let usernameIndex;

    for(let i = 0 ; i < userArray.length ; i++) {
        if (userEingabe == userArray[i].username) {
            usernameIndex = i;
            if (passwordEingabe == userArray[usernameIndex].password) {
                console.log('success');
                loginsuccess();
                currentUser = userArray[i];
                shoppingcart = get("/users/"+currentUser.id+"/shoppingcart");
                currentShoppingcart = getShoppingcart();
                renderArticles(allArticles);
                renderShoppingcart();
                renderBankaccounts();
                renderAllOrders();
                return;
            }
        }
    }
    alert('Login fehlgeschlagen!');
});
$('#logoutbtn').click(function(){
    window.location.reload();
})
    
$('#registerbtn').click(function() {
    let userData;
    $.ajax({
        type: "POST",
        url: "users",
        contentType: "application/json",
        async: false,
        dataType: "json",
        data: JSON.stringify({
            "username": $('#registerUsername').val(),
            "password": $('#registerPassword').val(),
            "honorifics": $('#registerHonorifics').val(),
            "name": $('#registerLastname').val(),
            "firstname": $('#registerFirstname').val(),
            "address": {
                "postcode": $('#registerPostcode').val(),
                "city": $('#registerCity').val(),
                "street":$('#registerStreet').val(),
                "housenumber": $('#registerHousenumber').val(),
            }
        }),
        success: function(res) {
            userData  = res;
            alert('Konto erfolgreich angelegt!');
            $('#registerUsername').val("")
            $('#registerPassword').val("");
            $('#registerHonorifics').val("");
            $('#registerLastname').val("");
            $('#registerFirstname').val("");
            $('#registerPostcode').val("");
            $('#registerCity').val("");
            $('#registerStreet').val("");
            $('#registerHousenumber').val("");
        },
        error: function(xhr) {
            let errMsg = JSON.parse(xhr.responseText);
            alert(errMsg[0].message);
        }
    });
    return userData;
});

//Order Shoppingcart
$('#orderbtn').click(function(){
    $.ajax({
        type: "POST",
        url: "/users/"+currentUser.id+"/orders/"+currentShoppingcart.id+"/",
        contentType: "application/json",
        data: JSON.stringify({
        }),
        async: false,
        dataType: "json",
        success: function(res) {
            articlesRes = res;
            alert('Die Bestellung wurde aufgegeben!');
        },
        error: function(xhr) {
            let errMsg = JSON.parse(xhr.responseText);
            alert(errMsg[0].message);
        }
    }) 
    $('#shoppingcarttable').empty();
    currentShoppingcart = getShoppingcart();
    renderShoppingcart();    
})

$('#addbank').click(function(){
    bankaccounts = get("/users/"+currentUser.id+"/bankaccounts");        
    console.log($("#BIC").val());
    $.ajax({
        type: "POST",
        url: "/users/"+currentUser.id+"/bankaccounts/",
        contentType: "application/json",
        data: JSON.stringify({
            "iban": $("#IBAN").val(),
            "bic": $("#BIC").val()
        }),
        async: false,
        dataType: "json",
        success: function(res) {
            articlesRes = res;
            alert('Das Konto wurde hinzugefügt!')
            $("#IBAN").val("")
            $("#BIC").val("")
            renderBankaccounts();
        },
        error: function(xhr) {
            let errMsg = JSON.parse(xhr.responseText);
            alert(errMsg[0].message);
        }
    })
    $('#bankaccountstable').empty();
    bankaccounts = get("/users/"+currentUser.id+"/bankaccounts");
    renderBankaccounts();
})

