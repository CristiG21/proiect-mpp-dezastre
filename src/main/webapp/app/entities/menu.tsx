import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate } from 'react-jhipster';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/center">
        <Translate contentKey="global.menu.entities.center" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/center-type-wrapper">
        <Translate contentKey="global.menu.entities.centerTypeWrapper" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/disaster">
        <Translate contentKey="global.menu.entities.disaster" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/photo-url">
        <Translate contentKey="global.menu.entities.photoUrl" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/community-message">
        <Translate contentKey="global.menu.entities.communityMessage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/official-message">
        <Translate contentKey="global.menu.entities.officialMessage" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
