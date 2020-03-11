import * as React from 'react';
import { Route } from 'react-router';
import { Link, Redirect } from 'react-router-dom';
import { useContext } from 'react';
import { IsAdminContext } from './App';
import { H1 } from './BasicStyles';


export function AuthRoute({component: Component, isLoggedIn, isAuthenticated, ...rest}) {
  const adminConsumer = useContext(IsAdminContext);
  return <Route {...rest} render={props => (
    (adminConsumer.role === 'ROLE_ADMIN' || adminConsumer.role === 'ROLE_MANAGER')
      ? <Component {...props}/>
      : <Redirect to={ '/noAccess'}/>
  )}/>
}


export function NoAccessComp() {
  return (
    <div style={{display: 'flex', justifyContent: 'center',alignItems: 'center', flexDirection: 'column'}}>
      <H1>Ingen tilgang</H1>
      <Link to={'/home'}>Trykk her for å returnere til heimesida</Link>
    </div>

  );
}