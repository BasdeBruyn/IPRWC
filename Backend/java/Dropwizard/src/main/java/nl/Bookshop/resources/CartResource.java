package nl.Bookshop.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import nl.Bookshop.exceptions.InvalidInputException;
import nl.Bookshop.models.CartItem;
import nl.Bookshop.models.Item;
import nl.Bookshop.models.User;
import nl.Bookshop.services.CartService;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/cart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CartResource {
    private final CartService cartService;

    public CartResource() {
        cartService = CartService.getInstance();
    }

    @GET
    @UnitOfWork
    @RolesAllowed("admin")
    public List<CartItem> getAll(){
        return cartService.getAll();
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public CartItem getById(@PathParam("id") Integer id, @Auth User authUser){
        return cartService.getById(id, authUser);
    }

    @GET
    @Path("/items/{userId}")
    @UnitOfWork
    public Item[] getItemsFromCart(@PathParam("userId") Integer userId, @Auth User authUser){
        return cartService.getItemsFromCart(userId, authUser);
    }

    @GET
    @Path("/from/{userId}")
    @UnitOfWork
    public List<CartItem> getCart(@PathParam("userId") Integer userId, @Auth User authUser) {
        return cartService.getCart(userId, authUser);
    }

    @POST
    @UnitOfWork
    public CartItem save(@Valid CartItem cartItem, @Auth User authUser) throws InvalidInputException {
        return cartService.save(cartItem, authUser);
    }

    @PUT
    @UnitOfWork
    public CartItem update(@Valid CartItem cartItem, @Auth User authUser) throws InvalidInputException {
        cartService.update(cartItem, authUser);

        return cartItem;
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public void delete(@PathParam("id") Integer id, @Auth User authUser) {
        cartService.delete(id, authUser);
    }

    @DELETE
    @Path("/from/{userId}")
    @UnitOfWork
    public void emptyCart(@PathParam("userId") Integer userId, @Auth User authUser) {
        cartService.emptyCart(userId, authUser);
    }
}
