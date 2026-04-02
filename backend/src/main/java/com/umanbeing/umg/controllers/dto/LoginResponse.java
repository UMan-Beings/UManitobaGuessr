package com.umanbeing.umg.controllers.dto;

/**
 * Data transfer object for user login response.
 * It is used for transferring user login response data between the controller and service layers.
 * <p>
 *     It must contain:
 *     <li>
 *         an authentication token for the user
 *     </li>
 *     <li>
 *         the username of the logged-in user
 *     </li>
 * </p>
 */
public record LoginResponse(
        String token,
        String name
) {

}
