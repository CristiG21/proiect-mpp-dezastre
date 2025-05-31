<!DOCTYPE html>
<html lang="${locale!''}">
<head>
  <meta charset="UTF-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <title>${properties.kcLoginTitle! "Sign In"}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />

  <!-- Google Font: Roboto -->
  <link
    href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap"
    rel="stylesheet"
  />

  <!-- Bootstrap 4.6 CSS -->
  <link
    rel="stylesheet"
    href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css"
  />

  <!-- Custom override (if you have additional custom.css) -->
  <link
    rel="stylesheet"
    href="${url.resourcesPath}/css/custom.css"
  />

  <style>
    /* Fallback: if you don’t have a custom.css, you can paste these few overrides here: */
    body {
      font-family: 'Roboto', sans-serif;
    }
    /* Increase card’s rounding and shadow */
    .card-modern {
      border: none;
      border-radius: 0.75rem;
      box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.1);
    }
    /* Tighten up spacing below the title */
    .card-header h1 {
      margin-bottom: 0.5rem;
    }
    /* Slightly larger logo */
    .card-header img {
      max-height: 120px;
      width: auto;
    }
    /* Red error text */
    .kc-error {
      color: #dc3545; /* Bootstrap’s “danger” color */
      font-size: 0.95rem;
      margin-bottom: 1rem;
      text-align: center;
    }
    /* Reduce vertical gaps between form groups */
    .form-group {
      margin-top: 0.75rem;
      margin-bottom: 0.75rem;
    }
    /* Space above the “Register” button */
    .btn-register {
      margin-top: 0.5rem;
    }
  </style>
</head>

<body class="d-flex align-items-center justify-content-center vh-100 bg-light">
<div class="w-100" style="max-width: 600px; width: 90%;">
  <div class="card card-modern">

    <!-- Card Header: Logo + Title -->
    <div class="card-header bg-white text-center py-4">
      <img
        src="${url.resourcesPath}/img/logo.png"
        alt="Logo"
      />
      <h1 class="h3 font-weight-bold">
        ${properties.kcLoginTitle! "Sign In"}
      </h1>
    </div>

    <!-- Card Body -->
    <div class="card-body px-4 py-4">

      <!-- 1) Show Keycloak error message (plain red text) -->
      <#if message?has_content>
        <div class="kc-error">
          ${kcSanitize(message.summary)?no_esc}
        </div>
      </#if>

      <form id="kc-form-login" action="${url.loginAction!''}" method="post">
        <!-- Username -->
        <div class="form-group">
          <label for="username" class="font-weight-medium">
            ${msg("username")! "Username"}
          </label>
          <input
            id="username"
            name="${username!'username'}"
            type="text"
            class="form-control form-control-lg"
            placeholder="${msg("username")! "Username"}"
            value="${username!''}"
            autofocus
          />
        </div>

        <!-- Password -->
        <div class="form-group">
          <label for="password" class="font-weight-medium">
            ${msg("password")! "Password"}
          </label>
          <input
            id="password"
            name="${password!'password'}"
            type="password"
            class="form-control form-control-lg"
            placeholder="${msg("password")! "Password"}"
          />
        </div>

        <!-- Remember Me (if allowed) -->
        <#if (rememberMeAllowed!false) == true>
          <div class="form-group form-check mt-3">
            <input
              id="rememberMe"
              name="${rememberMe!'rememberMe'}"
              type="checkbox"
              class="form-check-input"
              <#if (rememberMe!'') == "on">checked</#if>
            />
            <label class="form-check-label" for="rememberMe">
              ${msg("rememberMe")! "Remember Me"}
            </label>
          </div>
        </#if>

        <!-- Sign In Button -->
        <button
          type="submit"
          class="btn btn-primary btn-block btn-lg font-weight-medium mt-4"
        >
          ${msg("doLogIn")! "Sign In"}
        </button>

        <!-- Register Button -->
        <a
          href="${url.registrationUrl!''}"
          class="btn btn-outline-primary btn-block btn-lg font-weight-medium btn-register"
        >
          ${msg("doRegister")! "Register"}
        </a>
      </form>

      <hr class="mt-4" />

      <!-- Forgot Password Link (if allowed) -->
      <#if (forgotCredentialsAllowed!false) == true>
        <div class="text-center">
          <a
            href="${url.forgotCredentialsUrl!''}"
            class="text-muted"
            style="font-size: 0.95rem;"
          >
            ${msg("forgotYourPassword")! "Forgot your password?"}
          </a>
        </div>
      </#if>

    </div>
  </div>
</div>

<!-- jQuery & Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
