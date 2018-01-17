$('#shoppingcartbtn').hide();
$('#bankbtn').hide();
$('#profilebtn').hide();
$('#articles').hide();
$('#shoppingcart').hide();
$('#bankaccounts').hide();
$('#articlesearchbtn').hide();
$('#allOrdersbtn').hide();
$('#allOrders').hide();
$('#logoutbtn').hide()
$('#update').hide();

$('#articlesearchbtn').click(function(){
    console.log(currentUser);
    if(currentUser){
        $('#clara-login').hide();
        $('#register').hide();
        $('#shoppingcart').hide();
        $('#update').hide();        
        $('#allOrders').hide();        
        $('#bankaccountstable').hide();
        $('#bankaccounts').hide();            
        $('#articles').show();
        $('#articletable').empty();
        renderArticles(allArticles);        
    }
})

$('#allOrdersbtn').click(function(){
    $('#clara-login').hide();
    $('#update').hide();    
    $('#register').hide();
    $('#articles').hide();
    $('#shoppingcart').hide();
    $('#bankaccounts').hide();
    $('#allOrders').show();
    $('#allOrderstable').empty();    
    renderAllOrders();
})

$('#profilebtn').click(function(){
    $('#clara-login').hide();
    $('#update').hide();    
    $('#register').hide();
    $('#articles').hide();
    $('#shoppingcart').hide();
    $('#bankaccounts').hide();
    $('#allOrders').hide();
    currentProfile();
    $('#update').show();
 
})

$('#bankbtn').click(function(){
    if(currentUser){
        console.log(currentUser + "inside");
        $('#clara-login').hide();
        $('#allOrders').hide();                
        $('#register').hide();
        $('#update').hide();        
        $('#shoppingcart').hide();
        $('#articles').hide();
        $('#bankaccounts').show();
        $('#bankaccountstable').empty();
        renderBankaccounts();
        $('#bankaccountstable').show();
    }
})

$('#shoppingcartbtn').click(function(){
    $('#shoppingcart>p').hide();    
    $('#update').hide();    
    $('#clara-login').hide();
    $('#register').hide();
    $('#articles').hide();
    $('#bankaccounts').hide();  
    $('#allOrders').hide();
    $('#shoppingcart').show();
    $('#shoppingcarttable').empty();
    renderShoppingcart();
})

function loginsuccess(){
    $('#clara-login').hide();
    $('#register').hide();
    $('#shoppingcart').hide();
    $('#logoutbtn').show()    
    $('#articles').show();
    $('#shoppingcartbtn').show();
    $('#bankbtn').show();
    $('#profilebtn').show();
    $('#articlesearchbtn').show();
    $('#allOrdersbtn').show();
}