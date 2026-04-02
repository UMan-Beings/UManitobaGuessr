package com.umanbeing.umg.controllers.dto;

/**
 * Data transfer object for user sign-up response.
 * It is used for transferring sign-up response data between the controller and service layers.
 * <p>
 *     It must contain:
 *     <li>
 *         a success message
 *     </li>
 *     <li>
 *         the username of the newly registered user
 *     </li>
 * </p>
 */
public record SignUpResponse(
        String message,
        String name
) {

}
