import React from 'react';
import Redirect from 'react-router-dom/es/Redirect';
import { Button } from './Button';
import { FlexDiv, Input } from './BasicStyles';

export function Login() {
  const [userName, setUserName] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [error, setError] = React.useState('');
  const [redirect, setRedirect] = React.useState(false);
  if(redirect) {
    setTimeout(()=> {
      window.location.reload();
    },500);
    return <Redirect to={'/home'} />
  }
  return (
    <form onSubmit={(e) => {
      e.preventDefault();
      const request = new Request("http://localhost:8080/auth/login", {
        headers: {
          'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify({
          email: userName,
          password: password,
        }),
      });
      fetch(request).then(response => {
        // lacks authorization
        if (response.status === 401 || response.status === 403) {
          // invalid token
          console.error('Invalid login');
        }
        return response.json();
      }).then((data) => {
        localStorage.setItem('token', data.access_token);
        localStorage.setItem('role', data.roles[0]);
        setRedirect(true);
      }).catch((error) => {
        setError('Feil brukernamn/passord');
        console.error('Error:', error);
      });
    }}>
      <div>
        <div>Brukernamn</div>
        <Input type={'e-mail'} value={userName} onChange={(e) => setUserName(e.target.value)}/>
      </div>
      <div style={{marginTop: '1em'}}>
        <div>Passord</div>
        <Input type={'password'} value={password} onChange={(e) => setPassword(e.target.value)}/>
      </div>
      <FlexDiv style={{marginTop: '1em', justifyContent: 'flex-end' }}>
        <Button type={'submit'}>
          Login
        </Button>
      </FlexDiv>
      {error &&
      <div style={{marginTop: '.5em', color: 'red'}}>{error}</div>
      }
    </form>
  )
}