import React, { useContext, useEffect } from 'react';
import { FormLabel, Input, TextVerySmall } from './BasicStyles';
import { Dropdown } from './Dropdown';
import { IsAdminContext, superRequest } from './App';
import { Button } from './Button';

const hasNumber = value => {
  return new RegExp(/[0-9]/).test(value);
};
const hasMixed = value => {
  return new RegExp(/[a-z]/).test(value) &&
    new RegExp(/[A-Z]/).test(value);
};
const hasSpecial = value => {
  return new RegExp(/[!#@$%^&*)(+=._-]/).test(value);
};

export const strengthColor = count => {
  if (count < 2)
    return 'red';
  if (count < 3)
    return 'orange';
  if (count < 5)
    return 'green';

};

const strengthIndicator = value => {
  let strengths = 0;
  if (value.length > 5)
    strengths++;
  if (value.length > 7)
    strengths++;
  if (hasNumber(value))
    strengths++;
  if (hasSpecial(value))
    strengths++;
  if (hasMixed(value))
    strengths++;
  return strengths;
};

const passwordStrengthText = color => {
  switch (color) {
    case 'red':
      return 'Veldig svakt';
    case 'orange':
      return 'Middels';
    case 'green':
      return 'sterkt';

    default: return 'sterkt';

  }
};

const roles = [
  {value: '1', label: 'ROLE_USER'},
  {value: '2', label: 'ROLE_MANAGER'},
  {value: '3', label: 'ROLE_ADMIN'},
];
const personType = [
  {value: '1', label: 'CANDIDATE'},
  {value: '2', label: 'EXAMINATOR'},
  {value: '3', label: 'ADMIN'},
];

// const personType = [
//   {value: '1', label: 'Kandidat'},
//   {value: '2', label: 'Eksaminator'},
//   {value: '3', label: 'Admin'},
// ];
export function UserCreatePage() {
  const [comitees, setComitees] = React.useState();
  const adminConsumer = useContext(IsAdminContext);
  const [person, setPerson] = React.useState({
    email: '',
    name: '',
    password: '',
    role: '',
    personType: '',
    phoneNumber: '',
    company: '',
    region: '',
    registrationReceived: false,
    committees: ''
  });


  async function fetchUsers() {
    const fetch = await superRequest('http://localhost:8080/committee/list', {
      person
    });
    const res = await fetch;
    setComitees(res.map(c => {
      return {value: c.id, label: c.name};
    }));
  }


  useEffect(() => {
    fetchUsers();
  }, []);
  return (
    <div style={{margin: '0 auto'}}>
      <form onSubmit={async (e)=> {
        e.preventDefault();
        if(strengthColor(strengthIndicator(person.password)) === 'red') {
          return alert('For svakt passord');
        }
        await superRequest('http://localhost:8080/person/create', {
          ...person
        }).then(c => {
          window.location.reload();
        })

      }}>
      <div style={{marginTop: '1em'}}>
        <FormLabel>E-post</FormLabel>
      <Input style={{marginTop: '1em'}} type={'e-mail'} value={person.email} onChange={(e)=> setPerson({
        ...person,
        email: e.target.value
      })}/>
      </div>
      <div style={{marginTop: '1em'}}>
        <FormLabel>Navn</FormLabel>
        <Input style={{marginTop: '1em'}} type={'text'} value={person.name} onChange={(e)=> setPerson({
          ...person,
          name: e.target.value
        })}/>
      </div>
      <div style={{marginTop: '1em'}}>
        <FormLabel>Passord</FormLabel>
        <Input style={{marginTop: '1em'}} type={'password'} value={person.password} onChange={(e)=> setPerson({
          ...person,
          password: e.target.value
        })}/>
        {person.password.length > 0 &&
        <div style={{marginTop: '.5em', color: strengthColor(strengthIndicator(person.password)) ?? '#fff'}}>
          Passord styrke {' '}
          {passwordStrengthText(strengthColor(strengthIndicator(person.password)))}
        </div>
        }
      </div>
      <div style={{marginTop: '1em'}}>
        <FormLabel>Rolle</FormLabel>
       <Dropdown   onChange={(role)=> {
         if(role[0].label === 'ROLE_ADMIN') {
           if(adminConsumer === 'ROLE_ADMIN') {
             setPerson({
               ...person,
               role: role[0].label
             })
           } else {
             alert('Du har ikkje rettighenae til å lage en adminbruker');
             window.location.reload();
             setPerson({
               ...person,
               role: 'ROLE_USER'
             })
           }
         } else {
           setPerson({
             ...person,
             role: role[0].label
           })
         }

       }} multi={false} items={roles}/>
      </div>
      <div style={{marginTop: '1em'}}>
        <FormLabel>type person</FormLabel>
        <Dropdown onChange={(role)=> {
          if(role.label === 'Admin') {

          }
          setPerson({
            ...person,
            personType: role[0].label
          })
        }} multi={false} items={personType}/>
      </div>
      <div style={{marginTop: '1em'}}>
        <FormLabel>Telefon</FormLabel>
        <Input style={{marginTop: '1em'}} type={'number'} value={person.phoneNumber} onChange={(e)=> setPerson({
          ...person,
          phoneNumber: e.target.value
        })}/>

      </div>
      <div style={{marginTop: '1em'}}>
        <FormLabel>Bedrift</FormLabel>
        <Input style={{marginTop: '1em'}} type={'text'} value={person.company} onChange={(e)=> setPerson({
          ...person,
          company: e.target.value
        })}/>

      </div>
      <div style={{marginTop: '1em'}}>
        <FormLabel>region</FormLabel>
        <Input style={{marginTop: '1em'}} type={'text'} value={person.region} onChange={(e)=> setPerson({
          ...person,
          region: e.target.value
        })}/>
      </div>
      {person.personType === 'Kandidat' &&
      <div style={{marginTop: '1em'}}>
        <FormLabel>klar for eksamasjon</FormLabel>
        <Dropdown onChange={(role)=> {
          setPerson({
            ...person,
            registrationRecieved : role[0].label
          })
        }} multi={false} items={[{value: '1', label: 'Nei'}, {value: '1', label: 'Ja'}]}/>
      </div>
      }
      <div style={{marginTop: '1em'}}>
        <FormLabel>Fagfelt</FormLabel>
        <Dropdown onChange={(role)=> {
          setPerson({
            ...person,
            committees : role[0].value
          })
        }} multi={false} items={comitees}/>
      </div>
        <Button style={{marginTop: '1em'}} type={'submit'}>Oprett</Button>
      </form>
    </div>
  )
}
