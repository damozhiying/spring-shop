function renderArticles(itemList){    
    if(itemList.length == 0){
        $('#articletable').append('<p id="emptyAllOrders">Es wurde kein Artikel gefunden!</p>');
        return;
    }
    var table = $("#articletable").append('<table></table>');
    var tablebody = $("#articletable>table").append('<tbody></tbody>');    
    
    let heading = new Array();
    heading[0] = "Artikel"
    heading[1] = "Beschreibung"
    heading[2] = "Preis"

    var row = $("#articletable>table>tbody").append('<tr></tr>');
    heading.forEach(heading =>{
        var column = $("#articletable>table>tbody>tr").append('<th>'+heading+'</th>');
    })

    for(let i = 0 ; i < itemList.length ; i++){
        let selectAmount ="<select id="+itemList[i].id+"select>"+options+"</select>"
        $("#articletable>table>tbody").append('<tr><td>'+
        itemList[i].name+'</td><td>'+
        itemList[i].description+'</td><td>'+
        itemList[i].price.cents/100+".00€"+'</td><td>'+selectAmount+'</td><td><button id='+itemList[i].id+'articlebtn class="addbtn">Zum Warenkorb</button></td></tr>');
        $("#"+itemList[i].id+"articlebtn").click(function(){addToShoppingcart(itemList[i], $("#"+itemList[i].id+"select").val())});
    }
    
}

function renderAllOrders(){
    allOrders = get("users/"+currentUser.id+"/orders");
    if(allOrders.length == 1){
        $('#allOrderstable').append('<p id="emptyAllOrders">Keine Bestellungen vorhanden!</p>');
        return;
    }
    var table = $("#allOrderstable").append('<table></table>');
    var tablebody = $("#allOrderstable>table").append('<tbody></tbody>');    
    
    let heading = new Array();
    heading[0] = "ID"
    heading[1] = "Datum"
    heading[2] = "Artikel"
    heading[3] = "Artikelpreis"
    heading[4] = "Anzahl"
    heading[5] = "Gesamtpreis"

    var row = $("#allOrderstable>table>tbody").append('<tr></tr>');
    heading.forEach(heading =>{
        var column = $("#allOrderstable>table>tbody>tr").append('<th>'+heading+'</th>');
    })


    for(let i = 0 ; i < allOrders.length ; i++){
        if(!allOrders[i].isOrder){
            continue;
        }
        let orderItems = get("users/"+currentUser.id+"/orders/"+allOrders[i].id+"/orderitems");
        let articlesInOrder;
        let amountsInOrder;
        let articlePrices;
        if(orderItems){
            articlesInOrder = orderItems[0].article.name + '</br>';
            amountsInOrder = orderItems[0].articleAmount + '</br>';
            articlePrices = orderItems[0].article.price.cents/100+'.00€ </br>';
        }        
        for(let j = 1; j < orderItems.length; j++){
            articlesInOrder = articlesInOrder + orderItems[j].article.name+"<br />";
            amountsInOrder = amountsInOrder + orderItems[j].articleAmount+"<br />";
            articlePrices = articlePrices + orderItems[j].article.price.cents/100+".00€<br />";
            console.log(orderItems[j].article.name);
        }
        $("#allOrderstable>table>tbody").append('<tr><td><br /></td></tr><tr><td>'+
        allOrders[i].id+'</td><td>'+
        allOrders[i].date+'</td><td>'+
        articlesInOrder+'</td><td>'+
        articlePrices+'</td><td>'+
        amountsInOrder+'</td><td>'+
        allOrders[i].totalPrice.cents/100+".00€"+'</td><td>'+
        '<button id='+allOrders[i].id+'deleteorderbtn class="deleteorderbtn" type="button">Delete</button></td></tr>'); 
        $("#"+allOrders[i].id+"deleteorderbtn").click(function(){deleteOrder(allOrders[i].id)});  
    }
}

function renderShoppingcart(){
    shoppingcart = get("/users/"+currentUser.id+"/shoppingcart");
    if(shoppingcart.length == 0){
        $('#orderbtn').hide();
        $('#shoppingcarttable').append('<p id="emptyShoppingcart">Der Einkaufswagen ist leer!</p>');
        return;
    }
    $('#orderbtn').show();
    var table = $("#shoppingcarttable").append('<table></table>');
    var tablebody = $("#shoppingcarttable>table").append('<tbody></tbody>');    
    
    let heading = new Array();
    heading[0] = "Artikel"
    heading[1] = "Beschreibung"
    heading[2] = "Anzahl"
    heading[3] = "Preis"

    var row = $("#shoppingcarttable>table>tbody").append('<tr></tr>');
    heading.forEach(heading =>{
        var column = $("#shoppingcarttable>table>tbody>tr").append('<th>'+heading+'</th>');
    })

    for(let i = 0; i < shoppingcart.length ; i++){
        var selectOptions;
        if(shoppingcart[i].articleAmount){
            for(let j = 0; j < shoppingcart[i].articleAmount ;j++){
                selectOptions = selectOptions + "<option>"+(j+1)+"</option>";
            }
        }
        let selectAmount ="<select id="+shoppingcart[i].article.id+"selecttodelete>"+selectOptions+"</select>";
        selectOptions = "";        
        $("#shoppingcarttable>table>tbody").append('<tr><td>'+
        shoppingcart[i].article.name+'</td><td>'+
        shoppingcart[i].article.description+'</td><td>'+
        shoppingcart[i].articleAmount+'</td><td>'+
        shoppingcart[i].totalPrice.cents/100+".00€"+'</td><td>'+
        selectAmount+'<td><button id='+shoppingcart[i].article.id+'deletebtn class="deletebtn">Entfernen</button></tr>');
        $("#"+shoppingcart[i].article.id+"deletebtn").click(function(){deleteFromShoppingcart(shoppingcart[i].article, $("#"+shoppingcart[i].article.id+"selecttodelete").val())});        
    }
}



function renderBankaccounts(){
    
    bankaccounts = get("/users/"+currentUser.id+"/bankaccounts");    
    if(bankaccounts.length == 0){
        $('#bankaccountstable').append('<p id="noBankaccounts">Keine Konten verfügbar!</p>');
        return;
    }
    var table = $("#bankaccountstable").append('<table></table>');
    var tablebody = $("#bankaccountstable>table").append('<tbody></tbody>');    
    
    let heading = new Array();
    heading[0] = "IBAN"
    heading[1] = "BIC"

    var row = $("#bankaccountstable>table>tbody").append('<tr></tr>');
    heading.forEach(heading =>{
        var column = $("#bankaccountstable>table>tbody>tr").append('<th>'+heading+'</th>');
    })


    for(let i = 0 ; i < bankaccounts.length ; i++){
        $("#bankaccountstable>table>tbody").append('<tr><td>'+
        bankaccounts[i].iban+'</td><td>'+
        bankaccounts[i].bic+'</td><td>'+
        '<button id='+i+'deletebankbtn class="deletebankbtn" type="button">Delete</button></td></tr>'); 
        $("#"+i+"deletebankbtn").click(function(){deleteBankAccount(i)});  
    }  
}
