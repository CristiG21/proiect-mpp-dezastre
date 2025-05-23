import center from 'app/entities/center/center.reducer';
import centerTypeWrapper from 'app/entities/center-type-wrapper/center-type-wrapper.reducer';
import disaster from 'app/entities/disaster/disaster.reducer';
import photoURL from 'app/entities/photo-url/photo-url.reducer';
import communityMessage from 'app/entities/community-message/community-message.reducer';
import officialMessage from 'app/entities/official-message/official-message.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  center,
  centerTypeWrapper,
  disaster,
  communityMessage,
  photoURL,
  officialMessage,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
