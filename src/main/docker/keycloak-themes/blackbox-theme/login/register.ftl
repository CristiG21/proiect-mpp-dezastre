<!DOCTYPE html>
<html lang="${locale!''}">
<head>
  <meta charset="UTF-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <title>${properties.kcRegisterTitle! "Register"}</title>
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

  <!-- Custom override CSS (if you have one) -->
  <link
    rel="stylesheet"
    href="${url.resourcesPath}/css/custom.css"
  />

  <style>
    /* Match the same minimal “login” styling: */
    body {
      font-family: 'Roboto', sans-serif;
    }
    .card-modern {
      border: none;
      border-radius: 0.75rem;
      box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.1);
    }
    .card-header img {
      max-height: 120px;
      width: auto;
    }
    .card-header h1 {
      margin-top: 0.5rem;
      margin-bottom: 0.5rem;
    }
    .kc-error {
      color: #dc3545; /* Bootstrap “danger” red */
      font-size: 0.95rem;
      margin-bottom: 1rem;
      text-align: center;
    }
    .field-error {
      color: #dc3545;
      font-size: 0.875rem;
      margin-top: 0.25rem;
      display: block;
    }
    .form-group {
      margin-top: 0.75rem;
      margin-bottom: 0.75rem;
    }
    .btn-back {
      margin-top: 1rem;
    }
  </style>
</head>

<body class="d-flex align-items-center justify-content-center vh-100 bg-light">
<div class="w-100" style="max-width: 600px; width: 90%;">
  <div class="card card-modern">

    <!-- Card Header -->
    <div class="card-header bg-white text-center py-4">
      <img
        src="${url.resourcesPath}/img/logo.png"
        alt="Logo"
      />
      <h1 class="h3 font-weight-bold">
        ${properties.kcRegisterTitle! "Register"}
      </h1>
    </div>

    <!-- Card Body -->
    <div class="card-body px-4 py-4">

      <!-- Top‐level Keycloak error message (if any) -->
      <#if message?has_content>
        <div class="kc-error">
          ${kcSanitize(message.summary)?no_esc}
        </div>
      </#if>

      <form
        id="kc-form-register"
        action="${url.registrationAction!''}"
        method="post"
      >

        <!-- Username -->
        <div class="form-group">
          <label for="username" class="font-weight-medium">
            Username
          </label>
          <input
            id="username"
            name="username"
            type="text"
            class="form-control form-control-lg"
            placeholder="Username"
            value="${username!''}"
            autofocus
          />
          <#if errors?has_content && errors.username?has_content>
            <span class="field-error">
                ${kcSanitize(errors.username)?no_esc}
              </span>
          </#if>
        </div>

        <!-- Email -->
        <div class="form-group">
          <label for="email" class="font-weight-medium">
            Email
          </label>
          <input
            id="email"
            name="email"
            type="email"
            class="form-control form-control-lg"
            placeholder="Email"
            value="${email!''}"
          />
          <#if errors?has_content && errors.email?has_content>
            <span class="field-error">
                ${kcSanitize(errors.email)?no_esc}
              </span>
          </#if>
        </div>

        <!-- First Name & Last Name (only if email‐as‐username = false) -->
        <#--
          Keycloak always provides realm.registrationEmailAsUsername in the context.
          If it’s false, we ask for firstName/lastName. Otherwise, skip.
        -->
        <#if realm.registrationEmailAsUsername?has_content && (realm.registrationEmailAsUsername == false)>
          <div class="form-group">
            <label for="firstName" class="font-weight-medium">
              First Name
            </label>
            <input
              id="firstName"
              name="firstName"
              type="text"
              class="form-control form-control-lg"
              placeholder="First Name"
              value="${firstName!''}"
            />
            <#if errors?has_content && errors.firstName?has_content>
              <span class="field-error">
                  ${kcSanitize(errors.firstName)?no_esc}
                </span>
            </#if>
          </div>

          <div class="form-group">
            <label for="lastName" class="font-weight-medium">
              Last Name
            </label>
            <input
              id="lastName"
              name="lastName"
              type="text"
              class="form-control form-control-lg"
              placeholder="Last Name"
              value="${lastName!''}"
            />
            <#if errors?has_content && errors.lastName?has_content>
              <span class="field-error">
                  ${kcSanitize(errors.lastName)?no_esc}
                </span>
            </#if>
          </div>
        </#if>

        <!-- Password -->
        <div class="form-group">
          <label for="password" class="font-weight-medium">
            Password
          </label>
          <input
            id="password"
            name="password"
            type="password"
            class="form-control form-control-lg"
            placeholder="Password"
          />
          <#if errors?has_content && errors.password?has_content>
            <span class="field-error">
                ${kcSanitize(errors.password)?no_esc}
              </span>
          </#if>
        </div>

        <!-- Confirm Password -->
        <div class="form-group">
          <label for="password-confirm" class="font-weight-medium">
            Confirm Password
          </label>
          <input
            id="password-confirm"
            name="password-confirm"
            type="password"
            class="form-control form-control-lg"
            placeholder="Confirm Password"
          />
          <#-- Note: Keycloak’s errors map uses the key "password-confirm" (with hyphen) -->
          <#if errors?has_content && errors["password-confirm"]?has_content>
            <span class="field-error">
                ${kcSanitize(errors["password-confirm"])?no_esc}
              </span>
          </#if>
        </div>

        <!-- Register Button -->
        <button
          type="submit"
          class="btn btn-primary btn-block btn-lg font-weight-medium mt-4"
        >
          Register
        </button>
      </form>

      <a
        href="${url.loginUrl!''}"
        class="btn btn-outline-primary btn-block btn-lg font-weight-medium btn-register"
      >
        Sign In
      </a>

      <hr class="mt-4" />
    </div>
  </div>
</div>

<!-- jQuery & Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
