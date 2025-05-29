import './header.scss';

import React, { useState } from 'react';
import { Storage, translate, Translate } from 'react-jhipster';
import { Collapse, Nav, Navbar, NavbarToggler } from 'reactstrap';
import LoadingBar from 'react-redux-loading-bar';
import { Link, useLocation } from 'react-router-dom';
import { useAppDispatch } from 'app/config/store';
import { setLocale } from 'app/shared/reducers/locale';
import { AccountMenu, AdminMenu, EntitiesMenu, LocaleMenu } from '../menus';
import { Brand, Home } from './header-components';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faComments, faFlag } from '@fortawesome/free-solid-svg-icons';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isOpenAPIEnabled: boolean;
  currentLocale: string;
}

const Header = (props: IHeaderProps) => {
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();
  const isHomePage = location.pathname === '/';
  const dispatch = useAppDispatch();

  const handleLocaleChange = event => {
    const langKey = event.target.value;
    Storage.session.set('locale', langKey);
    dispatch(setLocale(langKey));
  };

  const renderDevRibbon = () =>
    props.isInProduction === false ? (
      <div className="ribbon dev">
        <a href="">
          <Translate contentKey={`global.ribbon.${props.ribbonEnv}`} />
        </a>
      </div>
    ) : null;

  const toggleMenu = () => setMenuOpen(!menuOpen);

  return (
    <div id="app-header">
      {/* {renderDevRibbon()} */}
      <LoadingBar className="loading-bar" />
      <Navbar data-cy="navbar" dark expand="md" fixed="top" className="jh-navbar">
        <NavbarToggler aria-label="Menu" onClick={toggleMenu} />
        <Brand />
        <Collapse isOpen={menuOpen} navbar>
          <Nav id="header-tabs" className="ms-auto" navbar>
            <Home />
            {isHomePage && (
              <li className="nav-item">
                <a className="nav-link" role="button" onClick={() => window.dispatchEvent(new Event('toggleFeed'))}>
                  <FontAwesomeIcon icon={faComments} className="me-1" />
                  {translate('disasterApp.center.home.feed')}
                </a>
              </li>
            )}
            {props.isAuthenticated && (
              <li className="nav-item">
                <Link to="/my-centers" className="nav-link">
                  <FontAwesomeIcon icon={faFlag} className="me-1" />
                  {translate('disasterApp.center.home.centers')}
                </Link>
              </li>
            )}
            {props.isAuthenticated && props.isAdmin && <EntitiesMenu />}
            {props.isAuthenticated && props.isAdmin && <AdminMenu showOpenAPI={props.isOpenAPIEnabled} />}
            <LocaleMenu currentLocale={props.currentLocale} onClick={handleLocaleChange} />
            <AccountMenu isAuthenticated={props.isAuthenticated} />
          </Nav>
        </Collapse>
      </Navbar>
    </div>
  );
};

export default Header;
