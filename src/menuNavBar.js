import {
  mdiMenu,
  mdiClockOutline,
  mdiCloud,
  mdiCrop,
  mdiAccount,
  mdiCogOutline,
  mdiEmail,
  mdiLogout,
  mdiThemeLightDark,
  mdiGithub,
  mdiReact,
  mdiTagMultiple,
} from '@mdi/js'

export default [
  {
    isCurrentUser: true,
    menu: [
      {
        icon: mdiAccount,
        label: 'My Profile',
        to: '/profile',
      },
      // {
      //   icon: mdiCogOutline,
      //   label: 'Settings',
      // },
      // {
      //   icon: mdiEmail,
      //   label: 'Messages',
      // },
      // {
      //   isDivider: true,
      // },
      // {
      //   icon: mdiLogout,
      //   label: 'Log Out',
      //   isLogout: true,
      // },
    ],
  },
  {
    icon: mdiThemeLightDark,
    label: 'Light/Dark',
    isDesktopNoLabel: true,
    isToggleLightDark: true,
  },
  // {
  //   icon: mdiLogout,
  //   label: 'Log out',
  //   isDesktopNoLabel: true,
  //   isLogout: true,
  // },
]
